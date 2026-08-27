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

package com.mongodb.jdbc.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.auth.exception.InvalidIssuerException;
import com.mongodb.jdbc.auth.exception.InvalidTokenException;
import com.mongodb.jdbc.auth.exception.UnsignedTokenException;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the MarkerEnforcer.
 *
 * <p>Note that this does not do signature tests, so test tokens are signed by randomly generated
 * keys.
 */
public class MarkerEnforcerTest {
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

    static final class TestMarkerProvider implements MarkerProvider {
        private final SignedJWT token;

        TestMarkerProvider(SignedJWT token) {
            this.token = token;
        }

        @Override
        public Optional<SignedJWT> getMarker() {
            return token != null ? Optional.of(this.token) : Optional.empty();
        }
    }

    @Test
    void validServiceMarkerAccepted() {
        String cluster = "example";
        SignedJWT marker = signMarker(generateMarker(cluster, MongoIssuer.SERVICE_ISSUER));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertTrue(enforcer.validate(cluster));
    }

    @Test
    void validEmergencyMarkerAccepted() {
        String cluster = "example";
        Date future = Date.from(Instant.now().plusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(generateExpiringMarker(cluster, MongoIssuer.EMERGENCY_ISSUER, future));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertTrue(enforcer.validate(cluster));
    }

    @Test
    void validExpiringServiceMarkerAccepted() {
        String cluster = "example";
        Date future = Date.from(Instant.now().plusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(generateExpiringMarker(cluster, MongoIssuer.SERVICE_ISSUER, future));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertTrue(enforcer.validate(cluster));
    }

    @Test
    void expiredServiceMarkerAccepted() {
        String cluster = "example";
        Date past = Date.from(Instant.now().minusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(generateExpiringMarker(cluster, MongoIssuer.SERVICE_ISSUER, past));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertTrue(enforcer.validate(cluster));
    }

    @Test
    void clusterMismatchDenied() {
        String cluster = "example";
        String otherCluster = "foo";

        SignedJWT marker = signMarker(generateMarker(cluster, MongoIssuer.SERVICE_ISSUER));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertFalse(enforcer.validate(otherCluster));
    }

    @Test
    void disabledMarkerDenied() {
        String cluster = "example";
        SignedJWT marker = signMarker(generateMarker(cluster, MongoIssuer.SERVICE_ISSUER, false));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertFalse(enforcer.validate(cluster));
    }

    @Test
    void expiredEmergencyDenied() {
        String cluster = "example";
        Date past = Date.from(Instant.now().minusSeconds(60 * 60));

        SignedJWT marker =
                signMarker(generateExpiringMarker(cluster, MongoIssuer.EMERGENCY_ISSUER, past));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertFalse(enforcer.validate(cluster));
    }

    @Test
    void noJwtThrows() {
        assertThrows(
                InvalidTokenException.class,
                () -> new MarkerEnforcer(new TestMarkerProvider(null)),
                "Attempting to validate a missing marker should throw an InvalidTokenException");
    }

    @Test
    void unsignedTokenThrows() {
        SignedJWT unsigned = generateMarker("foo", MongoIssuer.SERVICE_ISSUER);
        assertThrows(
                UnsignedTokenException.class,
                () -> new MarkerEnforcer(new TestMarkerProvider(unsigned)),
                "Attempting to validate an unsigned marker should throw an UnsignedTokenException");
    }

    @Test
    void invalidIssuerThrows() {
        String cluster = "example";
        SignedJWT marker = signMarker(generateMarker(cluster, "nonsense"));
        MarkerEnforcer enforcer = new MarkerEnforcer(new TestMarkerProvider(marker));

        assertThrows(InvalidIssuerException.class, () -> enforcer.validate(cluster));
    }
}
