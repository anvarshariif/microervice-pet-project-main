package uz.uzinfocom.product.redis;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {
  /*@Bean
  public ObjectMapper redisObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.findAndRegisterModules(); // Bu muhim!
    return objectMapper;
  }*/
  @Bean
  public ObjectMapper redisObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Bu qatorlarni qo'shing - TYPE INFORMATION uchun
    objectMapper.activateDefaultTyping(
        objectMapper.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL,
        JsonTypeInfo.As.PROPERTY
    );

    objectMapper.findAndRegisterModules();
    return objectMapper;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Custom ObjectMapper bilan serializer
    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(redisObjectMapper());

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);

    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    GenericJackson2JsonRedisSerializer serializer =
        new GenericJackson2JsonRedisSerializer(redisObjectMapper());

    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(1))
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
        )
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer)
        );

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .build();
  }



  /*@Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
    RedisCacheConfiguration config = RedisCacheConfiguration
        .defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(30)) // 30 minut dan keyin kesh tozalanadi.
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()
            )
        );

    return RedisCacheManager.builder(factory)
        .cacheDefaults(config)
        .build();
  }
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());  // <-- MUHIM!
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    //GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

    // Serializerlarni o‘rnatish
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);

    template.afterPropertiesSet();
    return template;
  }*/
/*
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Key serializer
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());

    // Value serializer
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

    template.afterPropertiesSet();
    return template;
  }*/
}