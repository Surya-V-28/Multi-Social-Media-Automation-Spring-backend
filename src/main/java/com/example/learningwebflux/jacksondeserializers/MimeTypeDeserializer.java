package com.example.learningwebflux.jacksondeserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.IOException;

@Component
public class MimeTypeDeserializer extends StdDeserializer<MimeType> {
    MimeTypeDeserializer() {
        super((Class<?>) null);
    }

    @Override
    public MimeType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return MimeType.valueOf(p.getValueAsString());
    }
}
