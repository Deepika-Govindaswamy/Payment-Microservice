package com.microservice.payment_gateway.configuration;

import com.microservice.payment_gateway.webclient.PaymentProcessorClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${payment-processor.url}")
    private String paymentProcessorUrl;

    @Bean
    public PaymentProcessorClient paymentProcessorClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(paymentProcessorUrl)
                .build();

        var restClientAdapter =  RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpServiceProxyFactory.createClient(PaymentProcessorClient.class);
    }
}
