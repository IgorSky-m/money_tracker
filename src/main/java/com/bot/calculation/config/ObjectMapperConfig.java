package com.bot.calculation.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


@Configuration
public class ObjectMapperConfig {

    /**
     * Получение синглтона маппера
     * @return синглтон маппера
     */
    @Bean
    public ObjectMapper objectMapper(){
        return JsonObjectMapperFactory.getInstance();
    }



    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder(ObjectMapper objectMapper){
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        builder.configure(objectMapper);
        return builder;
    }



    private static class JsonObjectMapperFactory {

        private static class SingletonObjectMapper{
            static final ObjectMapper instance = new ObjectMapper();
        }

        /**
         * Получить инстанс Jackson маппера
         * @return
         */
        public static ObjectMapper getInstance() {
            return SingletonObjectMapper.instance;
        }
    }

}

