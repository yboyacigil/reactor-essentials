package com.yboyacigil.reactor.essentials.bridge;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
class FluxBridgeTest {

    private EventProcessor eventProcessor;

    @BeforeEach
    void setUp() {
        eventProcessor = new EventProcessor();
    }

    @SneakyThrows
    @Test
    void testCreate() {
        val t1 = new Thread(() -> {
            while (eventProcessor.isRunning()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                val event = new Event(EventType.TYPE1, System.nanoTime());
                eventProcessor.process(event);
            }
        });
        t1.start();
        log.info("T1 started");

        val t2 = new Thread(() -> {
            while (eventProcessor.isRunning()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                val event = new Event(EventType.TYPE2, System.nanoTime());
                eventProcessor.process(event);
            }
        });
        t2.start();
        log.info("T2 started");

        val bridge = Flux.<Event>create(
            fluxSink -> eventProcessor.register(new EventListener<>() {
                @Override
                public void onData(Event data) {
                    fluxSink.next(data);
                }

                @Override
                public void complete() {
                    fluxSink.complete();
                }
            })
        );
        log.info("Flux bridge created");

        Scheduler s = Schedulers.newSingle("event-consumer");
        bridge.subscribeOn(s)
            .buffer(Duration.ofSeconds(5))
            .subscribe(events -> log.info("Consuming {} events in last 5 secs: {}", events.size(), events),
                t -> log.error("Error in consuming events", t),
                () -> log.info("Completed"));
        log.info("Subscribed on scheduler: {}", s);

        val t3 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException ignored) {
            }
            log.info("Shutting down event processor");
            eventProcessor.shutdown();
        });
        t3.start();

        StepVerifier.create(bridge)
            .expectNextCount(60L)
            .verifyComplete();

        t1.join();
        t2.join();
        t3.join();
    }
}
