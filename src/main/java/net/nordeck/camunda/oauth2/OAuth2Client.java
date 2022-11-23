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

import net.nordeck.camunda.oauth2.config.TokenValidator;
import net.nordeck.camunda.oauth2.dto.AccessToken;
import net.nordeck.camunda.oauth2.dto.RefreshToken;
import net.nordeck.camunda.oauth2.exception.OAuth2RuntimeException;
import net.nordeck.camunda.oauth2.exception.TokenResponseInvalidException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Handles token fetching and refreshing.
 */
public class OAuth2Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Client.class);
    private static final String ERROR_MESSAGE = "Unable to fetch tokens with response code ";

    private final TokenRequestFactory tokenRequestFactory;

    private final OkHttpClient httpClient;

    private final TokenValidator tokenValidator;

    private AccessToken accessToken;

    private RefreshToken refreshToken;

    /**
     * @param tokenRequestFactory the factory which creates the requests our client is using to get its tokens
     * @param httpClient an instance of okhttp3.OkHttpClient
     * @param tokenValidator the validator, to check if our token is expired
     */
    public OAuth2Client(TokenRequestFactory tokenRequestFactory, OkHttpClient httpClient, TokenValidator tokenValidator) {
        this.tokenRequestFactory = tokenRequestFactory;
        this.httpClient = httpClient;
        this.tokenValidator = tokenValidator;

        fetchTokens(tokenRequestFactory.createTokenRequest());
    }

    /**
     * This method returns the access token. If the existing token is expired, the refresh token is used to refresh get a new one.
     * @return the actual access token
     */
    public AccessToken getAccessToken() {
        if (tokenValidator.isExpired(accessToken)) {
            if (refreshToken.token() == null) {
                fetchTokens(tokenRequestFactory.createTokenRequest());
            } else {
                fetchTokens(tokenRequestFactory.createRefreshTokenRequest(refreshToken.token()));
            }
        }
        return accessToken;
    }

    /**
     * Fetches access and refresh token according to provided configuration.
     * @param request An instance of okhttp3.Request
     * @throws TokenResponseInvalidException when the token request was not successful.
     * @throws IOException if things are completely messed up.
     */
    private void fetchTokens(Request request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to fetch tokens using " + request);
        }

        try (Response response = httpClient.newCall(request).execute()) {
            if (isSuccessful(response)) {
                String responseBody = Objects.requireNonNull(response.body()).string();
                accessToken = new AccessToken(extractFromResponse("access_token", responseBody));
                refreshToken = new RefreshToken(extractFromResponse("refresh_token", responseBody));
                LOGGER.info("fetched tokens successfully.");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(accessToken.toString());
                    LOGGER.debug(refreshToken.toString());
                }
            } else {
                throw new TokenResponseInvalidException(ERROR_MESSAGE, String.valueOf(response.code()), response.message());
            }
        } catch (IOException e) {
            throw new OAuth2RuntimeException(e);
        }
    }

    /**
     * Validate that the response is successful. Means that the status code is 200 and the response body is not empty.
     * @param response The response to be validated
     * @return true if the response was successful  and the response code is equal to 200 and the response body
     * is not empty
     */
    private boolean isSuccessful(Response response) {
        return response.isSuccessful()
                && response.code() == 200
                && isNotEmpty(response.body());
    }

    /**
     * Helper method to get a value from JSON response body.
     * @param key the JSON key to gather the values for
     * @param responseBody the response bdy to parse
     * @return the key's value
     */
    private String extractFromResponse(String key, String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        return json.has(key) ? (String) json.get(key) : null;
    }

}
