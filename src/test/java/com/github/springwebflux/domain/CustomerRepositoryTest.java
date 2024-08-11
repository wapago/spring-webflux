package com.github.springwebflux.domain;

import com.github.springwebflux.DBInit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DBInit.class)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void 한건찾기_테스트() {
//        Mono<Customer> mCustomer = customerRepository.findById(2L);
        StepVerifier
                .create(customerRepository.findById(2L))
                .expectNextMatches((customer) -> {
                    return customer.getFirstName().equals("Chloe");
                })
                .expectComplete()
                .verify();
    }
}
