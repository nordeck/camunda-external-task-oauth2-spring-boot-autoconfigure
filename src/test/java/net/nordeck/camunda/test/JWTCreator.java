/*
 *  Copyright 2022 Nordeck IT + Consulting GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and  limitations
 *  under the License.
 *
 */
package net.nordeck.camunda.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static java.time.Instant.now;

public final class JWTCreator {

    private JWTCreator() {

    }

    public static String create() {
        try {
            Instant now = now();
            return JWT.create()
                    .withClaim("name", "Jane Doe")
                    .withClaim("email", "jane@example.com")
                    .withSubject("jane")
                    .withJWTId(UUID.randomUUID().toString())
                    .withIssuedAt(Date.from(now))
                    .withExpiresAt(Date.from(now.plus(5L, ChronoUnit.MINUTES)))
                    .sign(Algorithm.HMAC256("bla"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
