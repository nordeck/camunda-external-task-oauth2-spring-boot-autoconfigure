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

import net.nordeck.camunda.oauth2.time.Clock;
import net.nordeck.camunda.oauth2.time.NewDateClock;
import net.nordeck.camunda.oauth2.OAuth2Client;
import net.nordeck.camunda.oauth2.TokenRequestFactory;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is the main configuration of the OAuth2Client used.
 */
@Configuration
@EnableConfigurationProperties(OAuth2ConfigProperties.class)
public class OAuth2ClientAutoconfiguration {

    /**
     * Create a new instance of net.nordeck.camunda.oauth2.OAuth2Client.
     *
     * @param tokenRequestFactory - the TokenRequestFactory to use
     * @param httpClient - the OkHttpClient to use
     * @param tokenValidator - an instance of a validator to provide checks for the Token
     * @return a new instance of net.nordeck.camunda.oauth2.OAuth2Client
     */
    @Bean
    public OAuth2Client oAuth2Client(TokenRequestFactory tokenRequestFactory, OkHttpClient httpClient, TokenValidator tokenValidator) {
        return new OAuth2Client(tokenRequestFactory, httpClient, tokenValidator);
    }

    /**
     * Configure the org.camunda.bpm.client.interceptor.ClientRequestInterceptor to handle OAuth authentication.
     * @param oAuth2Client the client to provide the access token
     * @return an instance of net.nordeck.camunda.oauth2.config.OAuth2RequestInterceptor which will provide an auth
     * header for the client
     */
    @Bean
    public OAuth2RequestInterceptor oAuth2RequestInterceptor(OAuth2Client oAuth2Client) {
        return new OAuth2RequestInterceptor(oAuth2Client);
    }

    /**
     * Provide an instance of TokenRequestFactory.
     * @param oAuth2ConfigProperties a set of properties which configures net.nordeck.camunda.oauth2.TokenRequestFactory
     * @return an instance of net.nordeck.camunda.oauth2.TokenRequestFactory
     */
    @Bean
    public TokenRequestFactory tokenRequestFactory(OAuth2ConfigProperties oAuth2ConfigProperties) {
        return new TokenRequestFactory(oAuth2ConfigProperties);
    }

    /**
     * Provides an instance of NewDateClock.
     *
     * @return an instance of an implementation of net.nordeck.camunda.oauth2.time.Clock
     */
    @Bean
    public Clock clock() {
        return new NewDateClock();
    }

    /**
     * This method provides an instance of net.nordeck.camunda.oauth2.config.TokenValidator which is configured
     * to use the provided Clock instance
     * @param clock an instance of net.nordeck.camunda.oauth2.time.Clock
     * @return a configured token validator object
     */
    @Bean
    public TokenValidator tokenValidator(Clock clock) {
        return new TokenValidator(clock);
    }

}
