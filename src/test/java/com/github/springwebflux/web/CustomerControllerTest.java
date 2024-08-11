package com.github.springwebflux.web;

import com.github.springwebflux.domain.Customer;
import com.github.springwebflux.domain.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


@WebFluxTest
public class CustomerControllerTest {

    @MockBean
    private CustomerRepository customerRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void 한건찾기_테스트() {

        // given
        Mono<Customer> givenData = Mono.just(new Customer("Jack", "Bauer"));

        // stub -> 행동 지시
        Mockito.when(customerRepository.findById(1L)).thenReturn(givenData);

        webTestClient.get().uri("/customer/{id}", 1L)
                .exchange()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo("Jack")
                .jsonPath("$.lastName").isEqualTo("Bauer");
    }

}
