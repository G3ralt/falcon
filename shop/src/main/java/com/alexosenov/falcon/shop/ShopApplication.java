package com.alexosenov.falcon.shop;

import com.alexosenov.falcon.model.entity.Product;
import com.alexosenov.falcon.shop.messaging.ProductConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@SpringBootApplication
@EnableRedisRepositories(basePackages = "com.alexosenov.falcon")
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
    private final static String TOPIC = "stockProducts"; //Move to config

    @Bean
    public MessageListenerAdapter listenerAdapter(ProductConsumer consumer) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(consumer);
        adapter.setSerializer(new Jackson2JsonRedisSerializer<>(Product.class));
        return adapter;
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(MessageListenerAdapter listenerAdapter,
                                                           RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(TOPIC));
        return container;
    }

    @Bean
    public Jackson2JsonRedisSerializer<Product> jackson2JsonRedisSerializer() {
        return new Jackson2JsonRedisSerializer<>(Product.class);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory, Jackson2JsonRedisSerializer<Product> serializer) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setDefaultSerializer(serializer);
        template.setConnectionFactory(connectionFactory);
        return template;
    }

}
