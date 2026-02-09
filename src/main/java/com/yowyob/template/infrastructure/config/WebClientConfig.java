package com.yowyob.template.infrastructure.config;

import com.yowyob.template.domain.ports.out.ProductEventPublisherPort;
import com.yowyob.template.domain.model.Product;
import com.yowyob.template.infrastructure.adapters.outbound.external.client.StockApiClient;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Primary
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    @Bean
    public StockApiClient stockApiClient(WebClient.Builder builder, 
                                         @Value("${application.external.stock-service-url}") String url) {
                                            
        WebClient webClient = builder.baseUrl(url).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        
        return factory.createClient(StockApiClient.class);
    }

    @Bean
    @ConditionalOnMissingBean(ProductEventPublisherPort.class)
    public ProductEventPublisherPort noopProductEventPublisherPort() {
        return new ProductEventPublisherPort() {
            @Override
            public Mono<Void> publishProductCreated(Product product) {
                return Mono.empty();
            }

            @Override
            public Mono<Void> publishProductUpdated(Product product) {
                return Mono.empty();
            }

            @Override
            public Mono<Void> publishProductDeleted(Product product) {
                return Mono.empty();
            }
        };
    }
}