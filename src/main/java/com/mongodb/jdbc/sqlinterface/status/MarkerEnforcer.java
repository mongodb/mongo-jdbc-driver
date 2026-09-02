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

import com.mongodb.jdbc.logging.MongoLogger;
import com.mongodb.jdbc.sqlinterface.exception.*;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;

/**
 * The JWT enforcer ensures that entitlement markers given to enterprise clusters are valid.
 *
 * <p>Note: This does not currently do any form of signature checking. That work will be included
 * later once there is a shared package for client entitlement available. That package will also
 * replace all of this code.
 */
public class MarkerEnforcer {
    /**
     * Validate a token for the supplied cluster
     *
     * @param marker The marker to validate
     * @param forCluster The cluster being accessed that needs entitlement validation
     * @throws Exception If the token is not correctly shaped
     */
    public static void validate(MongoLogger logger, SignedJWT marker, String forCluster)
            throws Exception {
        if (marker == null) {
            logger.log(Level.WARNING, "Entitlement marker was null");
            throw new SQLInterfaceStatusInvalidException(
                    new IllegalArgumentException("marker cannot be null"));
        }
        if (marker.getState() != JWSObject.State.SIGNED) {
            logger.log(Level.WARNING, "Entitlement marker was unsigned");
            throw new SQLInterfaceStatusInvalidException(
                    new IllegalArgumentException("marker must be signed"));
        }

        JWTClaimsSet claims = MarkerEnforcer.getClaims(logger, marker);

        // Validate the issuer first
        MongoIssuer issuer = MarkerEnforcer.getIssuer(logger, claims);
        if (issuer.equals(MongoIssuer.EMERGENCY)) {
            // Emergency entitlement markers need to have an expiration date that isn't yet expired
            Instant now = Instant.now();
            Date expiry = claims.getExpirationTime();
            if (expiry == null) {
                logger.log(
                        Level.WARNING,
                        "Emergency entitlement marker is missing its expiration date");
                throw new SQLInterfaceStatusInvalidException(
                        new IllegalArgumentException("expiration date cannot be null"));
            }

            if (now.isAfter(expiry.toInstant())) {
                logger.log(
                        Level.WARNING,
                        String.format("Emergency entitlement marker has expired as of %s", expiry));
                throw new SQLInterfaceStatusInvalidException(
                        new IllegalArgumentException("marker cannot be expired"));
            }
        }

        // Validate that the marker is for the specified cluster
        //
        // Note that we explicitly check with case-insensitivity.
        String cluster = claims.getSubject();
        if (cluster == null) {
            logger.log(Level.WARNING, "Entitlement marker is missing the subject cluster");
            throw new SQLInterfaceStatusInvalidException(
                    new IllegalArgumentException("cluster name cannot be null"));
        }
        if (!cluster.equalsIgnoreCase(forCluster)) {
            logger.log(
                    Level.WARNING,
                    String.format(
                            "Entitlement marker was minted for '%s' which is not the current cluster '%s'",
                            cluster, forCluster));
            throw new SQLInterfaceStatusInvalidException(
                    new IllegalArgumentException("cluster name must match"));
        }

        // Validate that the claims are correct for a valid token.
        boolean isEnabled = MarkerEnforcer.getEnabled(logger, claims);
        if (!isEnabled) {
            logger.log(Level.WARNING, "Entitlement marker is explicitly disabled");
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
    static JWTClaimsSet getClaims(MongoLogger logger, SignedJWT marker)
            throws SQLInterfaceStatusInvalidException {
        try {
            return marker.getJWTClaimsSet();
        } catch (ParseException e) {
            logger.log(
                    Level.WARNING, "Entitlement marker is malformed: marker contained no claims");
            throw new SQLInterfaceStatusInvalidException(e);
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
    static boolean getEnabled(MongoLogger logger, JWTClaimsSet claims)
            throws SQLInterfaceStatusInvalidException {
        Boolean isEnabled;
        try {
            isEnabled = claims.getBooleanClaim("enabled");
        } catch (ParseException e) {
            logger.log(Level.WARNING, "Entitlement marker's enabled claim is not a boolean");
            throw new SQLInterfaceStatusInvalidException(e);
        }

        if (isEnabled == null) {
            logger.log(Level.WARNING, "Entitlement marker is missing required enabled claim");
            throw new SQLInterfaceStatusInvalidException(
                    new IllegalArgumentException("enabled cannot be null"));
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
    static MongoIssuer getIssuer(MongoLogger logger, JWTClaimsSet claims)
            throws SQLInterfaceStatusInvalidException {
        String issuer = claims.getIssuer();
        if (issuer == null) {
            logger.log(Level.WARNING, "Entitlement marker is missing required issuer claim");
            throw new SQLInterfaceStatusInvalidException(
                    new IllegalArgumentException("issuer cannot be null"));
        }

        try {
            return MongoIssuer.fromString(claims.getIssuer());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Issuer did not match valid options", e);
            throw new SQLInterfaceStatusInvalidException(e);
        }
    }
}
