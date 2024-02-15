package com.example.demospringintegration.integration.jpa;

import jakarta.persistence.EntityManagerFactory;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
public class JpaInboundITFlowConfig {
  private final EntityManagerFactory entityManagerFactory;

  public JpaInboundITFlowConfig(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @Bean
  public MessageChannel s3InboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public PollerSpec poller() {
    return Pollers.trigger(new PeriodicTrigger(Duration.ofSeconds(5)));
  }

  @Bean
  public IntegrationFlow jpaInboundAdapterFlow(@Autowired PollerSpec poller) {
    return IntegrationFlow.from(
            Jpa.inboundAdapter(this.entityManagerFactory).jpaQuery("SELECT n FROM Name n"),
            e -> e.poller(poller))
        .channel(s3InboundChannel())
        .get();
  }
}
