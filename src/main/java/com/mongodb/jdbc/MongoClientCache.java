/*
 * Copyright 2026-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import com.mongodb.client.MongoClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The MongoClients shared by connections with the same settings.
 *
 * <p>Clients are held strongly, since a client that's dropped without close() keeps its server
 * connections open. Past maxClients, the least recently used idle clients are closed; clients in
 * use never are.
 */
final class MongoClientCache {
    // get() reorders the map, so every access must hold lock.
    private final Map<MongoClientCacheKey, CachedClient> clients =
            new LinkedHashMap<>(16, 0.75f, true);
    private final Object lock = new Object();
    private int maxClients;

    private static final class MongoClientCacheKey {
        private final List<String> settings;
        // With no PEM path, the X.509 passphrase holds the PEM itself, so it's part of the identity.
        private final String x509Passphrase;

        MongoClientCacheKey(MongoConnectionProperties properties, char[] x509Passphrase) {
            this.settings = properties.generateKey();
            this.x509Passphrase = x509Passphrase == null ? null : String.valueOf(x509Passphrase);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MongoClientCacheKey)) {
                return false;
            }
            MongoClientCacheKey other = (MongoClientCacheKey) o;
            return settings.equals(other.settings)
                    && Objects.equals(x509Passphrase, other.x509Passphrase);
        }

        @Override
        public int hashCode() {
            return Objects.hash(settings, x509Passphrase);
        }
    }

    private static class CachedClient {
        final MongoClient client;
        // The open connections using client. Guarded by lock.
        int openConnections;

        CachedClient(MongoClient client) {
            this.client = client;
        }
    }

    MongoClientCache(int maxClients) {
        this.maxClients = maxClients;
    }

    /**
     * Opens a connection on the cached client for these settings, creating the client if needed.
     */
    MongoConnection connect(MongoConnectionProperties properties, char[] x509Passphrase)
            throws Exception {
        MongoClientCacheKey key = new MongoClientCacheKey(properties, x509Passphrase);
        MongoConnection connection = null;
        CachedClient cached;
        List<MongoClient> evicted;
        synchronized (lock) {
            cached = clients.get(key);
            if (cached == null) {
                // Created under the lock so concurrent connects with this key share one client.
                connection = new MongoConnection(properties, x509Passphrase);
                cached = new CachedClient(connection.getMongoClient());
                clients.put(key, cached);
            }
            // Count it before evicting so this connection's own client can't be evicted.
            cached.openConnections++;
            evicted = removeIdleClientsOverLimit();
        }
        for (MongoClient client : evicted) {
            client.close();
        }

        if (connection == null) {
            try {
                connection = new MongoConnection(cached.client, properties, x509Passphrase);
            } catch (Exception e) {
                release(cached);
                throw e;
            }
        }
        CachedClient connectionClient = cached;
        connection.setMongoClientRelease(() -> release(connectionClient));
        return connection;
    }

    /** Closes client if it's cached and no connection is using it. */
    void closeIfIdle(MongoClient client) {
        boolean removed;
        synchronized (lock) {
            removed = clients.values().removeIf(c -> c.client == client && c.openConnections == 0);
        }
        if (removed) {
            client.close();
        }
    }

    void closeAll() {
        synchronized (lock) {
            for (CachedClient cached : clients.values()) {
                cached.client.close();
            }
            clients.clear();
        }
    }

    int size() {
        synchronized (lock) {
            return clients.size();
        }
    }

    void setMaxClients(int max) {
        synchronized (lock) {
            maxClients = max;
        }
    }

    private void release(CachedClient cached) {
        List<MongoClient> evicted;
        synchronized (lock) {
            cached.openConnections--;
            evicted = removeIdleClientsOverLimit();
        }
        for (MongoClient client : evicted) {
            client.close();
        }
    }

    // The caller must hold lock, and close the returned clients after releasing it.
    private List<MongoClient> removeIdleClientsOverLimit() {
        List<MongoClient> removed = new ArrayList<>();
        Iterator<CachedClient> leastRecentlyUsedFirst = clients.values().iterator();
        while (clients.size() > maxClients && leastRecentlyUsedFirst.hasNext()) {
            CachedClient cached = leastRecentlyUsedFirst.next();
            if (cached.openConnections == 0) {
                leastRecentlyUsedFirst.remove();
                removed.add(cached.client);
            }
        }
        return removed;
    }
}
