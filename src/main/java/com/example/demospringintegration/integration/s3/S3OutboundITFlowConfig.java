package com.example.demospringintegration.integration.s3;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.aws.outbound.S3MessageHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3OutboundITFlowConfig {
  public static final String BUCKET_NAME = "my-bucket";

  @Bean
  public S3AsyncClient s3AsyncClient() {
    return S3AsyncClient.builder()
        .endpointOverride(URI.create("http://localhost:9000"))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create("integration", "password")))
        // Disable TLS/SSL to match MinIO default
        .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
        .httpClientBuilder(NettyNioAsyncHttpClient.builder())
        .region(Region.AP_NORTHEAST_1)
        .build();
  }

  @Bean
  S3MessageHandler s3MessageHandler(S3AsyncClient s3AsyncClient) {
    S3MessageHandler s3MessageHandler = new S3MessageHandler(s3AsyncClient, BUCKET_NAME);
    s3MessageHandler.setCommand(S3MessageHandler.Command.UPLOAD);
    s3MessageHandler.setKeyExpression(new LiteralExpression("foo"));

    return s3MessageHandler;
  }

  @Bean
  IntegrationFlow s3OutboundITFlow(S3AsyncClient s3AsyncClient) {
    return IntegrationFlow.from("s3InboundChannel")
        .transform(
            Message.class,
            message ->
                MessageBuilder.withPayload(message.getPayload().toString().getBytes())
                    .copyHeaders(message.getHeaders())
                    .build())
        .handle(s3MessageHandler(s3AsyncClient))
        .get();
  }
}
