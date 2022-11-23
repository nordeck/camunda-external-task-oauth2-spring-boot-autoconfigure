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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nordeck.camunda.oauth2.dto.AccessToken;
import net.nordeck.camunda.oauth2.time.Clock;
import net.nordeck.camunda.test.JWTCreator;
import net.nordeck.camunda.test.TokenResponse;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = NONE)
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class OAuth2ClientTest {

    private static final String OAUTH_TOKEN_PATH = "/token-uri";
    private static final String CAMUNDA_FETCH_AND_LOCK_PATH = "/engine-rest/external-task/fetchAndLock";

    private static final int ONE_DAY_IN_MILLISECONDS = 86400000;
    private static MockWebServer mockWebServer;
    private static final String ACCESS_TOKEN = JWTCreator.create();
    private static final String REFRESH_TOKEN = JWTCreator.create();
    private static final String REFRESHED_ACCESS_TOKEN = JWTCreator.create();

    @MockBean
    private Clock jwtClock;

    @Autowired
    private OAuth2Client oAuth2Client;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("oauth_host", () -> "http://localhost:" + mockWebServer.getPort());
    }

    @BeforeAll
    public static void prepareMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        ObjectMapper objectMapper = new ObjectMapper();
        TokenResponse accessTokenResponse = new TokenResponse(ACCESS_TOKEN, REFRESH_TOKEN);
        TokenResponse refreshAccessTokenResponse = new TokenResponse(REFRESHED_ACCESS_TOKEN, REFRESH_TOKEN);

        Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {
                try {
                    switch (request.getPath()) {
                        case OAUTH_TOKEN_PATH:
                            String body = request.getBody().readUtf8();
                            if (body.contains("refresh_token")) {
                                return new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(refreshAccessTokenResponse));
                            }
                            return new MockResponse().setResponseCode(200).setBody(objectMapper.writeValueAsString(accessTokenResponse));
                        case CAMUNDA_FETCH_AND_LOCK_PATH:
                            return new MockResponse().setResponseCode(200).setBody("[]");
                    }
                } catch (JsonProcessingException e) {
                    // ignored
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        mockWebServer.setDispatcher(dispatcher);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    public void initDefaultJwtClock() {
        when(jwtClock.now()).thenReturn(new Date());
    }

    @Test
    public void thatTokensAreFetched() {
        AccessToken fetchedAccessToken = oAuth2Client.getAccessToken();
        assertEquals(ACCESS_TOKEN, fetchedAccessToken.token());
    }

    @Test
    public void thatTokenIsRefreshedWhenExpired() {
        AccessToken accessToken = oAuth2Client.getAccessToken();
        assertEquals(ACCESS_TOKEN, accessToken.token());

        Date oldNow = jwtClock.now();
        when(jwtClock.now()).thenReturn(new Date(oldNow.getTime() + ONE_DAY_IN_MILLISECONDS));

        AccessToken refreshedAccessToken = oAuth2Client.getAccessToken();
        assertEquals(REFRESHED_ACCESS_TOKEN, refreshedAccessToken.token());
    }
}
