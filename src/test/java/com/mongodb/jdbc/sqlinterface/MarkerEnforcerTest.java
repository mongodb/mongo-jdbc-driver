/*
 * Copyright 2024-present MongoDB, Inc.
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

package com.mongodb.jdbc.sqlinterface;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.sqlinterface.exception.*;
import com.mongodb.jdbc.sqlinterface.status.MarkerEnforcer;
import com.mongodb.jdbc.sqlinterface.status.MongoIssuer;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MarkerEnforcer.
 *
 * <p>Note that this does not do signature tests, so test tokens are signed by randomly generated
 * keys.
 */
public class MarkerEnforcerTest {
    private final MongoLogger logger = new MongoLogger(Logger.getLogger("test-logger"), 0);

    public MarkerEnforcerTest() {}

    SignedJWT generateMarker(String cluster, String issuer) {
        return generateMarker(cluster, issuer, true);
    }

    SignedJWT generateMarker(String cluster, String issuer, boolean enabled) {
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .claim("enabled", enabled)
                        .issuer(issuer)
                        .issueTime(new Date(123000L))
                        .subject(cluster)
                        .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.Ed25519).keyID("1").build();

        return new SignedJWT(header, claimsSet);
    }

    SignedJWT generateExpiringMarker(String cluster, String issuer, Date exp) {
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .claim("enabled", true)
                        .expirationTime(exp)
                        .issuer(issuer)
                        .issueTime(new Date(123000L))
                        .subject(cluster)
                        .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.Ed25519).keyID("1").build();

        return new SignedJWT(header, claimsSet);
    }

    SignedJWT generateMalformedMarker(String cluster, String issuer, Boolean enabled) {
        JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder().issueTime(new Date(123000L));
        if (cluster != null) {
            claimsSet = claimsSet.subject(cluster);
        }
        if (issuer != null) {
            claimsSet = claimsSet.issuer(issuer);
        }
        if (enabled != null) {
            claimsSet = claimsSet.claim("enabled", enabled);
        }

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.Ed25519).keyID("1").build();
        return new SignedJWT(header, claimsSet.build());
    }

    SignedJWT signMarker(SignedJWT marker) {
        try {
            OctetKeyPair key = new OctetKeyPairGenerator(Curve.Ed25519).generate();
            JWSSigner signer = new Ed25519Signer(key);
            marker.sign(signer);

            return marker;
        } catch (Exception e) {
            throw new RuntimeException("Signing should not fail: %s", e);
        }
    }

    @Test
    void validServiceMarkerAccepted() throws Exception {
        String cluster = "example";
        SignedJWT marker = signMarker(generateMarker(cluster, MongoIssuer.SERVICE.toIssuer()));

        MarkerEnforcer.validate(logger, marker, cluster);
    }

    @Test
    void validEmergencyMarkerAccepted() throws Exception {
        String cluster = "example";
        Date future = Date.from(Instant.now().plusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(
                        generateExpiringMarker(cluster, MongoIssuer.EMERGENCY.toIssuer(), future));

        MarkerEnforcer.validate(logger, marker, cluster);
    }

    @Test
    void validExpiringServiceMarkerAccepted() throws Exception {
        String cluster = "example";
        Date future = Date.from(Instant.now().plusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(generateExpiringMarker(cluster, MongoIssuer.SERVICE.toIssuer(), future));

        MarkerEnforcer.validate(logger, marker, cluster);
    }

    @Test
    void expiredServiceMarkerAccepted() throws Exception {
        String cluster = "example";
        Date past = Date.from(Instant.now().minusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(generateExpiringMarker(cluster, MongoIssuer.SERVICE.toIssuer(), past));

        MarkerEnforcer.validate(logger, marker, cluster);
    }

    @Test
    void clusterMismatchThrows() {
        String cluster = "example";
        String otherCluster = "foo";

        SignedJWT marker = signMarker(generateMarker(cluster, MongoIssuer.SERVICE.toIssuer()));

        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, marker, otherCluster));
    }

    @Test
    void missingEnabledThrows() {
        String cluster = "example";
        SignedJWT marker =
                signMarker(generateMalformedMarker(cluster, MongoIssuer.SERVICE.toIssuer(), null));

        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, marker, cluster));
    }

    @Test
    void missingIssuerThrows() {
        String cluster = "example";
        SignedJWT marker = signMarker(generateMalformedMarker(cluster, null, true));

        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, marker, cluster));
    }

    @Test
    void missingClusterThrows() {
        String cluster = "example";
        SignedJWT marker =
                signMarker(generateMalformedMarker(null, MongoIssuer.SERVICE.toIssuer(), true));

        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, marker, cluster));
    }

    @Test
    void disabledMarkerThrows() {
        String cluster = "example";
        SignedJWT marker =
                signMarker(generateMarker(cluster, MongoIssuer.SERVICE.toIssuer(), false));

        assertThrows(
                SQLInterfaceStatusDisabledException.class,
                () -> MarkerEnforcer.validate(logger, marker, cluster));
    }

    @Test
    void expiredEmergencyThrows() {
        String cluster = "example";
        Date past = Date.from(Instant.now().minusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(generateExpiringMarker(cluster, MongoIssuer.EMERGENCY.toIssuer(), past));

        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, marker, cluster));
    }

    @Test
    void noJwtThrows() {
        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, null, "_"),
                "Attempting to validate a missing marker should throw an InvalidTokenException");
    }

    @Test
    void unsignedTokenThrows() {
        SignedJWT unsigned = generateMarker("foo", MongoIssuer.SERVICE.toIssuer());
        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, unsigned, "_"),
                "Attempting to validate an unsigned marker should throw an UnsignedTokenException");
    }

    @Test
    void invalidIssuerThrows() {
        String cluster = "example";
        SignedJWT marker = signMarker(generateMarker(cluster, "nonsense"));

        assertThrows(
                SQLInterfaceStatusInvalidException.class,
                () -> MarkerEnforcer.validate(logger, marker, cluster));
    }
}
