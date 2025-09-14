package com.fbs.search.config;

import com.fbs.search.service.FlightEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisEventConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                       MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic("flight-events"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(FlightEventListener listener) {
        return new MessageListenerAdapter(listener, "handleFlightEvent");
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("flight-events");
    }
}