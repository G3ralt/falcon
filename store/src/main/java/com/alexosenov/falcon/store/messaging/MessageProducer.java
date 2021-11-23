package com.alexosenov.falcon.store.messaging;

import com.alexosenov.falcon.model.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final static String TOPIC = "stockProducts"; //move to config
    private final static Logger LOG = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private RedisTemplate<?,?> redisTemplate;

    public void sendMessage(Product product) {
        LOG.info("Sending message: {}", product);
        redisTemplate.convertAndSend(TOPIC, product);
    }

}
