package com.alexosenov.falcon.store;

import com.alexosenov.falcon.model.entity.Product;
import com.alexosenov.falcon.model.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@SpringBootApplication
@EnableRedisRepositories(basePackages = "com.alexosenov.falcon")
public class StoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory, Jackson2JsonRedisSerializer<Product> serializer) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setDefaultSerializer(serializer);
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public CommandLineRunner createInitialData(ProductRepository productRepository) {
        return args -> {
            Iterable<Product> all = productRepository.findAll();
            if (!all.iterator().hasNext()) {
                productRepository.save(Product.builder().name("Banana").quantity(4).build());
                productRepository.save(Product.builder().name("Chocolate").quantity(3).build());
            }
        };
    }

    @Bean
    public Jackson2JsonRedisSerializer<Product> jackson2JsonRedisSerializer() {
        return new Jackson2JsonRedisSerializer<>(Product.class);
    }
}
