package com.example.order_service.client;

import com.example.order_service.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            RestClient.Builder builder,
            @Value("${services.product.url}") String url
    ) {
        this.restClient = builder
                .baseUrl(url)
                .build();
    }

    public ProductResponse getById(Integer id) {
        return restClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .body(ProductResponse.class);
    }
}
