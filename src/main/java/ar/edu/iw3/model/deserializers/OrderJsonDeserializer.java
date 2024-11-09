package ar.edu.iw3.model.deserializers;

import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.OrderDeserializationException;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.util.JsonUtiles;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static ar.edu.iw3.util.JsonConstants.*;

public class OrderJsonDeserializer extends StdDeserializer<Order> {

    private static final long serialVersionUID = -3881285352118964728L;

    private IClientBusiness clientBusiness;
    private IDriverBusiness driverBusiness;
    private ITruckBusiness truckBusiness;
    private ITankBusiness tankBusiness;
    private IProductBusiness productBusiness;

    protected OrderJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    public OrderJsonDeserializer(Class<Order> vc, IClientBusiness clientBusiness, IDriverBusiness driverBusiness, ITruckBusiness truckBusiness, IProductBusiness productBusiness, ITankBusiness tankBusiness) {
        super(vc);
        this.clientBusiness = clientBusiness;
        this.driverBusiness = driverBusiness;
        this.truckBusiness = truckBusiness;
        this.productBusiness = productBusiness;
    }

    @Override
    public Order deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        Order order = new Order();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            // Obtener valores del nodo
            String externalId = JsonUtiles.getString(node, ORDER_NUMBER, "");
            Date expectedChargeDate = JsonUtiles.getDate(node, EXPECTED_CHARGE_DATE, null);
            float preset = JsonUtiles.getFloat(node, PRESET, 0);
            Client client = JsonUtiles.getClient(node, CLIENT, Client.class);
            Driver driver = JsonUtiles.getDriver(node, DRIVER, Driver.class);
            Product product = JsonUtiles.getProduct(node, PRODUCT, Product.class);
            Truck truck = JsonUtiles.getTruck(node, TRUCK, Truck.class);
            List<Tank> tanks = JsonUtiles.getTank(node, TANK, Tank.class);

            // Asignar valores a la orden
            order.setExternalId(externalId);
            order.setExpectedChargeDate(expectedChargeDate);
            order.setPreset(preset);
            order.setClient(client);
            order.setDriver(driver);
            order.setProduct(product);

            // Validar y asignar camión y tanques
            if (truck != null) {
                if (tanks != null) {
                    for (Tank tank : tanks) {
                        tank.setTruck(truck);
                    }
                    truck.setTanks(tanks);
                }
                order.setTruck(truck);
            }
            System.out.println(order.getTruck());
            System.out.println(order.getTruck().getTanks());

        } catch (Exception e) {
            throw new OrderDeserializationException("Error al deserializar la orden: " + e.getMessage(), e);
        }
        return order;
    }
}
