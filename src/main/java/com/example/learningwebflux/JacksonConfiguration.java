package com.example.learningwebflux;

import com.example.learningwebflux.jacksondeserializers.MimeTypeDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.MimeType;

@Configuration
public class JacksonConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper(MimeTypeDeserializer mimeTypeDeserializer) {
        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());

        var module = new SimpleModule();
        module.addDeserializer(MimeType.class, mimeTypeDeserializer);

        return objectMapper;
    }
}
