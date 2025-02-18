package ar.edu.iw3.model.serializers;

import ar.edu.iw3.model.Alarm;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class AlarmJsonSerializer extends StdSerializer<Alarm> {

    private static final long serialVersionUID = -3881285352118964728L;

    public AlarmJsonSerializer(Class<Alarm> t) { super(t); }

    @Override
    public void serialize(Alarm alarm, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("alarmId", String.valueOf(alarm.getId()));
        jsonGenerator.writeStringField("dateOccurrence", alarm.getDateOccurrence().toString());
        jsonGenerator.writeStringField("dateResolved", alarm.getDateResolved().toString());
        jsonGenerator.writeStringField("description", alarm.getDescription());
        jsonGenerator.writeStringField("status", alarm.getStatus().toString());
        jsonGenerator.writeNumberField("temperature", alarm.getTemperature());
        jsonGenerator.writeStringField("productName", alarm.getOrder().getProduct().getName());
        jsonGenerator.writeStringField("userName", alarm.getUser().getUsername());
        jsonGenerator.writeStringField("clientName", alarm.getOrder().getClient().getCompanyName());
        jsonGenerator.writeStringField("orderEId", alarm.getOrder().getExternalId());
        jsonGenerator.writeEndObject();
    }
}
