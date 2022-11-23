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

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nordeck.camunda.oauth2.config.TokenValidator;
import net.nordeck.camunda.oauth2.dto.AccessToken;
import net.nordeck.camunda.oauth2.exception.OAuth2RuntimeException;
import net.nordeck.camunda.oauth2.exception.TokenResponseInvalidException;
import net.nordeck.camunda.test.JWTCreator;
import net.nordeck.camunda.test.TokenResponse;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OAuth2ClientExceptionTest {

    private static final String ACCESS_TOKEN = JWTCreator.create();
    private static final String REFRESH_TOKEN = JWTCreator.create();

    private static final TokenRequestFactory TOKEN_REQUEST_FACTORY = mock(TokenRequestFactory.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String HTTP_URL_COM = "http://url.com";
    private static MockWebServer MOCK_WEB_SERVER;

    @BeforeAll
    public static void initMockWebserver() throws IOException {
        MOCK_WEB_SERVER = new MockWebServer();
        MOCK_WEB_SERVER.start();
    }

    @AfterAll
    public static void shutdown() throws IOException {
        MOCK_WEB_SERVER.shutdown();
    }

    @Test
    public void getAccessToken() throws IOException {
        TokenResponse accessTokenResponse = new TokenResponse(ACCESS_TOKEN, REFRESH_TOKEN);
        OkHttpClient httpClient = mockHttpClient(200, accessTokenResponse);
        when(TOKEN_REQUEST_FACTORY.createTokenRequest()).thenReturn(createRequest());
        OAuth2Client oAuth2Client = new OAuth2Client(TOKEN_REQUEST_FACTORY, httpClient, mock(TokenValidator.class));

        AccessToken fetchedAccessToken = oAuth2Client.getAccessToken();

        assertEquals(ACCESS_TOKEN, fetchedAccessToken.token());
    }

    @Test
    public void getAccessToken_IsExpired() throws IOException {
        TokenResponse accessTokenResponse = new TokenResponse(ACCESS_TOKEN, REFRESH_TOKEN);
        OkHttpClient httpClient = mockHttpClient(200, accessTokenResponse);
        when(TOKEN_REQUEST_FACTORY.createTokenRequest()).thenReturn(createRequest());
        TokenValidator tokenValidator = mock(TokenValidator.class);
        when(tokenValidator.isExpired(any())).thenReturn(true);

        OAuth2Client oAuth2Client = new OAuth2Client(TOKEN_REQUEST_FACTORY, httpClient, tokenValidator);
        when(tokenValidator.isExpired(any())).thenReturn(false);
        AccessToken accessToken = oAuth2Client.getAccessToken();
        assertEquals(ACCESS_TOKEN, accessToken.token());
    }

    @Test
    public void getAccessToken_Not200() throws IOException {
        TokenResponse accessTokenResponse = new TokenResponse(ACCESS_TOKEN, REFRESH_TOKEN);
        OkHttpClient httpClient = mockHttpClient(201, accessTokenResponse);
        when(TOKEN_REQUEST_FACTORY.createTokenRequest()).thenReturn(createRequest());

        TokenResponseInvalidException tokenResponseInvalidException = assertThrows(TokenResponseInvalidException.class, () -> new OAuth2Client(TOKEN_REQUEST_FACTORY, httpClient, mock(TokenValidator.class)));
        assertThat(tokenResponseInvalidException.getMessage()).contains("Unable to fetch tokens with response code (201):");
    }

    @Test
    public void getAccessToken_500() throws IOException {
        TokenResponse accessTokenResponse = new TokenResponse(ACCESS_TOKEN, REFRESH_TOKEN);
        OkHttpClient httpClient = mockHttpClient(500, accessTokenResponse);
        when(TOKEN_REQUEST_FACTORY.createTokenRequest()).thenReturn(createRequest());

        TokenResponseInvalidException tokenResponseInvalidException = assertThrows(TokenResponseInvalidException.class, () -> new OAuth2Client(TOKEN_REQUEST_FACTORY, httpClient, mock(TokenValidator.class)));
        assertThat(tokenResponseInvalidException.getMessage()).contains("Unable to fetch tokens with response code (500): ");
    }

    @Test
    public void getAccessToken_ThrowsIO() throws IOException {
        TokenResponse accessTokenResponse = new TokenResponse(ACCESS_TOKEN, null);
        OkHttpClient httpClient = mockHttpClient(500, accessTokenResponse);
        when(httpClient.newCall(any()).execute()).thenThrow(new IOException("Exception Message here!"));
        when(TOKEN_REQUEST_FACTORY.createTokenRequest()).thenReturn(createRequest());

        OAuth2RuntimeException oAuth2RuntimeException = assertThrows(OAuth2RuntimeException.class, () -> new OAuth2Client(TOKEN_REQUEST_FACTORY, httpClient, mock(TokenValidator.class)));
        assertThat(oAuth2RuntimeException.getMessage()).contains("java.io.IOException: Exception Message here!");
    }

    private static OkHttpClient mockHttpClient(final int responseCode, final TokenResponse tokenResponse) throws IOException {
        final OkHttpClient okHttpClient = mock(OkHttpClient.class);
        final Call remoteCall = mock(Call.class);
        String tokenAsJson = OBJECT_MAPPER.writeValueAsString(tokenResponse);
        MediaType mediaType = MediaType.parse("application/json");
        ResponseBody responseBody = ResponseBody.create(tokenAsJson, mediaType);

        final Response response = new Response.Builder()
                .request(new Request.Builder()
                        .url(HTTP_URL_COM)
                        .build())
                .protocol(Protocol.HTTP_1_1)
                .code(responseCode)
                .message("{}")
                .body(responseBody)
                .build();

        when(remoteCall.execute()).thenReturn(response);
        when(okHttpClient.newCall(any())).thenReturn(remoteCall);

        return okHttpClient;
    }

    private static Request createRequest() {
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", "some_client_id")
                .add("client_secret", " some_client_secret")
                .add("grant_type", "client_credentials")
                .add("scope", "some_scope")
                .build();

        return new Request.Builder()
                .url("http://localhost:" + MOCK_WEB_SERVER.getPort() + "/some/path")
                .post(formBody)
                .build();
    }

}
