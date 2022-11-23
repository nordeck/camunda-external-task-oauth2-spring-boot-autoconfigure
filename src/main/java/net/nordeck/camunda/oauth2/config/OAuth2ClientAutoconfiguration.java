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

@Configuration
@EnableConfigurationProperties(OAuth2ConfigProperties.class)
public class OAuth2ClientAutoconfiguration {

    @Bean
    public OAuth2Client oAuth2Client(TokenRequestFactory tokenRequestFactory, OkHttpClient httpClient, TokenValidator tokenValidator) {
        return new OAuth2Client(tokenRequestFactory, httpClient, tokenValidator);
    }

    @Bean
    public OAuth2RequestInterceptor oAuth2RequestInterceptor(OAuth2Client oAuth2Client) {
        return new OAuth2RequestInterceptor(oAuth2Client);
    }

    @Bean
    public TokenRequestFactory tokenRequestFactory(OAuth2ConfigProperties oAuth2ConfigProperties) {
        return new TokenRequestFactory(oAuth2ConfigProperties);
    }

    @Bean
    public Clock clock() {
        return new NewDateClock();
    }

    @Bean
    public TokenValidator tokenValidator(Clock clock) {
        return new TokenValidator(clock);
    }

}
