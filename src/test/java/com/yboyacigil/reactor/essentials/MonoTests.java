package com.yboyacigil.reactor.essentials;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
class MonoTests {

    @Test
    void testMono() {
        val name = "Yusuf";

        val mono = Mono.just(name)
            .log();

        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    void testMonoSubscribe() {
        val name = "Yusuf";

        val mono = Mono.just(name);

        log.info("Subscribing mono: {}", mono);
        mono.subscribe();

        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    void testMonoSubscribeAndConsume() {
        val name = "Yusuf";

        val mono = Mono.just(name);

        log.info("Subscribing mono: {}", mono);
        mono.subscribe(s -> log.info("Got value: {}", s));

        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    void testMonoSubscribeAndConsumeAndErrorConsume() {
        val name = "Yusuf";

        val mono = Mono.error(() -> new RuntimeException("Explicit error"));

        log.info("Subscribing mono: {}", mono);
        mono.subscribe(
            s -> log.info("Got value: {}", s),
            t -> log.error("Error in consuming mono", t)
        );

        StepVerifier.create(mono)
            .expectError(RuntimeException.class);
    }

    @Test
    void testMonoSubscribeAndConsumeAndErrorConsumeAndComplete() {
        val name = "Yusuf";

        val mono = Mono.just(name);

        log.info("Subscribing mono: {}", mono);
        mono.subscribe(
            s -> log.info("Got value: {}", s),
            t -> log.error("Error in consuming mono", t),
            () -> log.info("Completed")
        );

        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

    @Test
    void testMonoSubscribeAndConsumeAndErrorConsumeAndCompleteAndSubscription() {
        val name = "Yusuf";

        val mono = Mono.just(name)
            .log();

        log.info("Subscribing mono: {}", mono);
        mono.subscribe(
            s -> log.info("Got value: {}", s),
            t -> log.error("Error in consuming mono", t),
            () -> log.info("Completed"),
            Subscription::cancel
        );

        log.info("Step verifier");
        StepVerifier.create(mono)
            .expectNext(name)
            .verifyComplete();
    }

}
