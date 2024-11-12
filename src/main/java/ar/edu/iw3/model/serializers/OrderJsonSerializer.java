package ar.edu.iw3.model.serializers;

import ar.edu.iw3.model.Order;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class OrderJsonSerializer extends StdSerializer<Order> {

    private static final long serialVersionUID = -3881285352118964728L;

    public OrderJsonSerializer(Class<Order> t) {
        super(t);
    }

    @Override
    public void serialize(Order order, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("orderId", String.valueOf(order.getId()));
        jsonGenerator.writeNumberField("preset", order.getPreset());

        // Inicia el objeto "product" dentro del JSON
        jsonGenerator.writeObjectFieldStart("product");
        jsonGenerator.writeStringField("name", order.getProduct().getName());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndObject();
    }
}
