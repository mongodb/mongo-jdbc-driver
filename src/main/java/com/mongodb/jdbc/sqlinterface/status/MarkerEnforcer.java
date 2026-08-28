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

package com.mongodb.jdbc.sqlinterface.status;

import com.mongodb.jdbc.sqlinterface.exception.*;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JWT enforcer ensures that entitlement markers given to enterprise clusters are valid.
 *
 * <p>Note: This does not currently do any form of signature checking. That work will be included
 * later once there is a shared package for client entitlement available. That package will also
 * replace all of this code.
 */
public class MarkerEnforcer {
    private static final Logger log = LoggerFactory.getLogger(MarkerEnforcer.class);

    /**
     * Validate a token for the supplied cluster
     *
     * @param marker The marker to validate
     * @param forCluster The cluster being accessed that needs entitlement validation
     * @throws SQLInterfaceStatusException If the token is not correctly shaped
     */
    public static void validate(SignedJWT marker, String forCluster)
            throws SQLInterfaceStatusException {
        if (marker == null) {
            log.warn("Entitlement marker was null");
            throw new SQLInterfaceStatusInvalidException();
        }
        if (marker.getState() != JWSObject.State.SIGNED) {
            log.warn("Entitlement marker was unsigned");
            throw new SQLInterfaceStatusInvalidException();
        }

        JWTClaimsSet claims = MarkerEnforcer.getClaims(marker);

        // Validate the issuer first
        MongoIssuer issuer = MarkerEnforcer.getIssuer(claims);
        if (issuer.equals(MongoIssuer.EMERGENCY)) {
            // Emergency entitlement markers need to have an expiration date that isn't yet expired
            Instant now = Instant.now();
            Date expiry = claims.getExpirationTime();
            if (expiry == null) {
                log.warn("Emergency entitlement marker is missing its expiration date");
                throw new SQLInterfaceStatusInvalidException();
            }

            if (now.isAfter(expiry.toInstant())) {
                log.warn("Emergency entitlement marker has expired as of {}", expiry);
                throw new SQLInterfaceStatusInvalidException();
            }
        }

        // Validate that the marker is for the specified cluster
        //
        // Note that we explicitly check with case-insensitivity.
        String cluster = claims.getSubject();
        if (cluster == null) {
            log.warn("Entitlement marker is missing the subject cluster");
            throw new SQLInterfaceStatusInvalidException();
        }
        if (!cluster.equalsIgnoreCase(forCluster)) {
            log.warn(
                    "Entitlement marker was minted for {} which is not the current cluster {}",
                    cluster,
                    forCluster);
            throw new SQLInterfaceStatusInvalidException();
        }

        // Validate that the claims are correct for a valid token.
        boolean isEnabled = MarkerEnforcer.getEnabled(claims);
        if (!isEnabled) {
            log.warn("Entitlement marker is explicitly disabled");
            throw new SQLInterfaceStatusDisabledException();
        }
    }

    /**
     * Extract the claims from an entitlement marker
     *
     * @param marker The entitlement marker
     * @return The set of claims for the marker
     * @throws SQLInterfaceStatusInvalidException If the marker contains malformed or missing claims
     */
    static JWTClaimsSet getClaims(SignedJWT marker) throws SQLInterfaceStatusInvalidException {
        try {
            return marker.getJWTClaimsSet();
        } catch (ParseException e) {
            log.warn("Entitlement marker is malformed: marker contained no claims");
            throw new SQLInterfaceStatusInvalidException();
        }
    }

    /**
     * Extract the enabled field from an entitlement marker's claims
     *
     * @param claims The claims from the entitlement marker
     * @return The enabled field
     * @throws SQLInterfaceStatusInvalidException If the enabled field is not a boolean or is
     *     missing
     */
    static boolean getEnabled(JWTClaimsSet claims) throws SQLInterfaceStatusInvalidException {
        Boolean isEnabled;
        try {
            isEnabled = claims.getBooleanClaim("enabled");
        } catch (ParseException e) {
            log.warn("Entitlement marker's enabled claim is not a boolean");
            throw new SQLInterfaceStatusInvalidException();
        }

        if (isEnabled == null) {
            log.warn("Entitlement marker is missing required enabled claim");
            throw new SQLInterfaceStatusInvalidException();
        }

        return isEnabled;
    }

    /**
     * Parses the issuer of a set of claims
     *
     * @param claims The claims that should include an issuer
     * @return The issuer
     * @throws SQLInterfaceStatusInvalidException If the issuer is not provided or invalid
     */
    static MongoIssuer getIssuer(JWTClaimsSet claims) throws SQLInterfaceStatusInvalidException {
        String issuer = claims.getIssuer();
        if (issuer == null) {
            throw new SQLInterfaceStatusInvalidException();
        }

        try {
            return MongoIssuer.fromString(claims.getIssuer());
        } catch (ParseException e) {
            log.warn("Could not parse issuer", e);
            throw new SQLInterfaceStatusInvalidException();
        }
    }
}
