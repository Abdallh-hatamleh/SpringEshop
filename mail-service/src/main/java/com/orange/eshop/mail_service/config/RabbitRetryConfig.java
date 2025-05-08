//package com.orange.eshop.mail_service.config;
//
//
//import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
//import org.springframework.context.annotation.Bean;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.retry.interceptor.RetryOperationsInterceptor;
//
//
//@Configuration
//public class RabbitRetryConfig {
//
//    @Bean
//    public RetryOperationsInterceptor retryOperationsInterceptor() {
//        return RetryInterceptorBuilder.stateless()
//                .maxAttempts(3)
//                .backOffOptions(5000, 2.0, 10000)
//                .recoverer(new RejectAndDontRequeueRecoverer())
//                .build();
//    }
//
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory,
//            RetryOperationsInterceptor retryOperationsInterceptor) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setAdviceChain(retryOperationsInterceptor);
//        return factory;
//    }
//}
