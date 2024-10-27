package ar.edu.iw3.model.deserializers;

import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.util.JsonUtiles;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Date;

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

        long idOrder = JsonUtiles.getLong(node, ORDER_NUMBER, 0);
        Date expectedChargeDate = JsonUtiles.getDate(node, EXPECTED_CHARGE_DATE, null);
        float preset = JsonUtiles.getFloat(node, PRESET, 0);

        order.setId(idOrder);
        order.setExpectedChargeDate(expectedChargeDate);
        order.setPreset(preset);

//        if (driver != null){
//            try {
//                order.setDriver(driverBusiness.findOrCreate(driver));
//            } catch (BusinessException e) {
//                throw new RuntimeException(e);
//            }
//        }
        Client client;
        Driver driver;
        Product product;
        Truck truck;
        try {
            client = JsonUtiles.getClient(node, CLIENT, Client.class);
            driver = JsonUtiles.getDriver(node, DRIVER, Driver.class);
            product = JsonUtiles.getProduct(node, PRODUCT, Product.class);
            truck = JsonUtiles.getTruck(node, TRUCK, Truck.class);
        } catch (BusinessException e) {
            throw new RuntimeException(e);
        }
        // TODO: como hacemos con los tank?

        order.setDriver(driver);
        order.setTruck(truck);
        order.setClient(client);
        order.setProduct(product);

        return order;
    }
}
