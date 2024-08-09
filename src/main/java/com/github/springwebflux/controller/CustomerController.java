package com.github.springwebflux.controller;

import com.github.springwebflux.domain.Customer;
import com.github.springwebflux.domain.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
public class CustomerController {
    private final CustomerRepository customerRepository;
    private final Sinks.Many<Customer> sink;



    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        sink = Sinks.many().multicast().onBackpressureBuffer(); // 모든 클라이언트의 플럭스 요청을 sink할 수 있음.
    }

    // 1,2,3,4,5 한번에 출력
    @GetMapping("/flux")
    public Flux<Integer> flux() {
        return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
    }

    // 1,2,3,4,5 순차적 출력
    @GetMapping(value = "/fluxstream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)  // APPLICATION_STREAM_JSON_VALUE
    public Flux<Integer> fluxstream() {
        return Flux.just(1,2,3,4,5).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/customer", produces = MediaType.TEXT_EVENT_STREAM_VALUE)  // APPLICATION_STREAM_JSON_VALUE)
    public Flux<Customer> findAll() {
        return customerRepository.findAll().delayElements(Duration.ofSeconds(1)).log();
    }

    // data가 1건일 때는 Mono로 리턴
    @GetMapping("/customer/{id}")
    public Mono<Customer> findById(@PathVariable Long id) {
        return customerRepository.findById(id).log();
    }

    @GetMapping(value = "/customer/sse") // 생략 produces = MediaType.TEXT_EVENT_STREAM_VALUE
    public Flux<ServerSentEvent<Customer>> findAllSSE() {
        return sink.asFlux().map(customer -> ServerSentEvent.builder(customer).build()).doOnCancel(() -> {
            sink.asFlux().blockLast();
        });
    }

    @PostMapping("/customer")
    public Mono<Customer> save() {
        return customerRepository.save(new Customer("CHANHO", "KIM")).doOnNext(customer -> {
            sink.tryEmitNext(customer);
        });
    }
}
