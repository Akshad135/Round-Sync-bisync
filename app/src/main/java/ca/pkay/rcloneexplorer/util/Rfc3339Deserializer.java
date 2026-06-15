package ca.pkay.rcloneexplorer.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;

import ca.pkay.rcloneexplorer.RcloneRcd;


public class Rfc3339Deserializer extends StdDeserializer<Long> {



    protected Rfc3339Deserializer() {
        super(Rfc3339Deserializer.class);

    }

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode timeNode = parser.getCodec().readTree(parser);
        try {
            return Rfc3339Helper.parseCalendar(timeNode.asText()).getTimeInMillis();
        } catch (ParseException e) {
            return 0L;
        }
    }
}
