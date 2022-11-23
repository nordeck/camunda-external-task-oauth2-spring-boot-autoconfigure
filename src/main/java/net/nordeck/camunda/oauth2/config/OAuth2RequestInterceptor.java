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

import net.nordeck.camunda.oauth2.OAuth2Client;
import net.nordeck.camunda.oauth2.dto.AccessToken;
import org.camunda.bpm.client.interceptor.ClientRequestContext;
import org.camunda.bpm.client.interceptor.ClientRequestInterceptor;


/**
 * org.camunda.bpm.client.interceptor.ClientRequestInterceptor implementation to set the Authorization properly.
 */
public class OAuth2RequestInterceptor implements ClientRequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final OAuth2Client oauthClient;

    /**
     *
     * @param oauthClient the client which shall be provided with an interceptor
     */
    public OAuth2RequestInterceptor(OAuth2Client oauthClient) {
        this.oauthClient = oauthClient;
    }

    /**
     * This method creates and adds an authorization header for the given context
     * @param requestContext the context the interceptor has to work on
     */
    @Override
    public void intercept(ClientRequestContext requestContext) {
        AccessToken accessToken = oauthClient.getAccessToken();
        requestContext.addHeader(AUTHORIZATION_HEADER, "Bearer " + accessToken.token());
    }
}
