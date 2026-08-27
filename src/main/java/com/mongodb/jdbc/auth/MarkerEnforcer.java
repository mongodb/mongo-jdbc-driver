/*
 * Copyright 2022-present MongoDB, Inc.
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

package com.mongodb.jdbc.auth;

import com.mongodb.jdbc.auth.exception.InvalidTokenException;
import com.mongodb.jdbc.auth.exception.MissingClaimException;
import com.mongodb.jdbc.auth.exception.UnsignedTokenException;
import com.mongodb.jdbc.auth.exception.ValidationException;
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

    final SignedJWT token;

    public MarkerEnforcer(MarkerProvider markerProvider) throws ValidationException {
        SignedJWT token =
                markerProvider
                        .getMarker()
                        .orElseThrow(
                                () -> new InvalidTokenException("Entitlement marker is missing"));
        if (token.getState() != JWSObject.State.SIGNED) {
            throw new UnsignedTokenException();
        }

        this.token = token;
    }

    /**
     * Validate a token for the supplied cluster
     *
     * @param forCluster The cluster being accessed that needs entitlement validation
     * @return Whether the token is valid for the cluster
     * @throws ValidationException If the token is not correctly shaped
     */
    public boolean validate(String forCluster) throws ValidationException {
        JWTClaimsSet claims = this.getClaims(this.token);

        // Validate the issuer first
        MongoIssuer issuer = this.getIssuer(claims);
        if (issuer.equals(MongoIssuer.EMERGENCY)) {
            // Emergency entitlement markers need to have an expiration date that isn't yet expired
            Instant now = Instant.now();
            Date expiry = claims.getExpirationTime();
            if (expiry == null) {
                log.warn("Emergency entitlement marker is missing its expiration date");
                throw new MissingClaimException("exp");
            }

            if (now.isAfter(expiry.toInstant())) {
                log.warn("Emergency entitlement marker has expired");
                return false;
            }
        }

        // Validate that the marker is for the specified cluster
        String cluster = claims.getSubject();
        if (cluster == null) {
            throw new MissingClaimException("sub");
        }
        if (!cluster.equals(forCluster)) {
            log.warn("Entitlement marker was not minted for the current cluster");
            return false;
        }

        // Validate that the claims are correct for a valid token.
        return this.getEnabled(claims);
    }

    JWTClaimsSet getClaims(SignedJWT token) throws InvalidTokenException {
        try {
            return token.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }

    boolean getEnabled(JWTClaimsSet claims) throws ValidationException {
        try {
            return claims.getBooleanClaim("enabled");
        } catch (ParseException e) {
            throw new ValidationException("Entitlement marker's enabled claim is not a boolean");
        }
    }

    MongoIssuer getIssuer(JWTClaimsSet claims) throws ValidationException {
        String issuer = claims.getIssuer();
        if (issuer == null) {
            throw new MissingClaimException("iss");
        }

        return MongoIssuer.fromString(claims.getIssuer());
    }
}
