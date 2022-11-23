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
package net.nordeck.camunda.oauth2;

import net.nordeck.camunda.oauth2.config.OAuth2ConfigProperties;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * This factory object creates the requests our client is using to get access and refresh tokens
 */
public class TokenRequestFactory {

    private final OAuth2ConfigProperties oAuth2ConfigProperties;

    /**
     * @param oAuth2ConfigProperties
     */
    public TokenRequestFactory(OAuth2ConfigProperties oAuth2ConfigProperties) {
        this.oAuth2ConfigProperties = oAuth2ConfigProperties;
    }

    /**
     * creates an access token request with its oauth2 properties
     * @return a correctly configured access token request
     */
    public Request createTokenRequest() {
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", oAuth2ConfigProperties.getClientId())
                .add("client_secret", oAuth2ConfigProperties.getClientSecret())
                .add("grant_type", "client_credentials")
                .add("scope", oAuth2ConfigProperties.getScope())
                .build();

        return new Request.Builder()
                .url(oAuth2ConfigProperties.getTokenUri())
                .post(formBody)
                .build();
    }

    /**
     * creates a refresh token request with its oauth2 properties
     * @param refreshToken the request's refresh token
     * @return a correctly configured refresh token request
     */
    public Request createRefreshTokenRequest(String refreshToken) {
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", oAuth2ConfigProperties.getClientId())
                .add("client_secret", oAuth2ConfigProperties.getClientSecret())
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
                .build();

        return new Request.Builder()
                .url(oAuth2ConfigProperties.getTokenUri())
                .post(formBody)
                .build();
    }
}
