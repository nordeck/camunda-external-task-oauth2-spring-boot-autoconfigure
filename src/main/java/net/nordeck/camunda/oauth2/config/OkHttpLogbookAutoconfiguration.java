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

import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.okhttp.LogbookInterceptor;

/**
 * Log Config? Und noch?
 */
@Configuration
@ConditionalOnClass(LogbookInterceptor.class)
public class OkHttpLogbookAutoconfiguration {

    /**
     * This method provides an instance of okhttp3.OkHttpClient, which is configured to use the logbook interceptor
     * @param logbook the instance of org.zalando.logbook.Logbook which shall be used to log your messages
     * @return a configured instance of okhttp3.OkHttpClient
     */
    @Bean
    public OkHttpClient httpClient(Logbook logbook) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new LogbookInterceptor(logbook))
                .build();
    }

}
