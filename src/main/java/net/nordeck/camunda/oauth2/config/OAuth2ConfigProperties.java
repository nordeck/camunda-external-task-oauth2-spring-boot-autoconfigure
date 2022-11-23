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

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;


/**
 * This class configures the core components of our client
 */
@ConfigurationProperties(prefix = "oauth2")
@Validated
@ConstructorBinding
public class OAuth2ConfigProperties {
    @NotBlank
    private final String issuerUri;
    @NotBlank
    private final String tokenUri;
    @NotBlank
    private final String clientId;
    @NotBlank
    private final String clientSecret;

    @NotBlank
    private final String scope;


    /**
     *  a provided set of OAuth2 properties
     * @param issuerUri the issuer
     * @param tokenUri the uri of the token
     * @param clientId the preconfigured client id
     * @param clientSecret the preconfigured secret
     * @param scope a scope to be defined
     */
    public OAuth2ConfigProperties(String issuerUri,
                                  String tokenUri,
                                  String clientId,
                                  String clientSecret,
                                  String scope) {
        this.issuerUri = issuerUri;
        this.tokenUri = tokenUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
    }

    public String getIssuerUri() {
        return issuerUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScope() {
        return scope;
    }
}
