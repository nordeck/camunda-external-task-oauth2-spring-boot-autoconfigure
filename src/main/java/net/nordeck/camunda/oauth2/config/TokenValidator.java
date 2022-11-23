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
package net.nordeck.camunda.oauth2.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.nordeck.camunda.oauth2.dto.AccessToken;
import net.nordeck.camunda.oauth2.time.Clock;

/**
 * This object creates a validator for JWTs
 */
public class TokenValidator {

    private final Clock clock;

    public TokenValidator(Clock clock) {
        this.clock = clock;
    }

    /**
     * This method checks if a provided access token is expired
     * @param accessToken the token which should get validated
     * @return true if the getExpriesAt field comes before now(), meaning if it is expired
     */
    public boolean isExpired(AccessToken accessToken) {
        DecodedJWT decodedJWT = JWT.decode(accessToken.token());
        return decodedJWT.getExpiresAt().before(clock.now());
    }
}
