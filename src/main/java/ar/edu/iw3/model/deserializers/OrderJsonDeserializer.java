package ar.edu.iw3.model.deserializers;

import ar.edu.iw3.model.*;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.*;
import ar.edu.iw3.util.JsonUtiles;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import static ar.edu.iw3.util.JsonConstants.*;

public class OrderJsonDeserializer extends StdDeserializer<Order> {

    private static final long serialVersionUID = -3881285352118964728L;

    private IClientBusiness clientBusiness;
    private IDriverBusiness driverBusiness;
    private ITruckBusiness truckBusiness;
    //private ITankBusiness tankBusiness;
    private IProductBusiness productBusiness;

    protected OrderJsonDeserializer(Class<?> vc) {
        super(vc);
    }

    public OrderJsonDeserializer(Class<Order> vc, IClientBusiness clientBusiness, IDriverBusiness driverBusiness, ITruckBusiness truckBusiness, IProductBusiness productBusiness) {
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
        Date expectedChargeDate = JsonUtiles.getDate(node, EXPECTED_CHARGE_DATE, String.valueOf(LocalDate.now().plusDays(5))); // TODO: hacemos esto o lo dejamos en null?
        float preset = JsonUtiles.getFloat(node, PRESET, 0);
        Driver driver = JsonUtiles.getObject(node, DRIVER, Driver.class);
        if (driver != null){
            try {
                order.setDriver(driverBusiness.find(driver.getId()));
            } catch (BusinessException | NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Driver: " + driver);
        Truck truck = JsonUtiles.getObject(node, TRUCK, Truck.class);
        Client client = JsonUtiles.getObject(node, CLIENT, Client.class);
        Product product = JsonUtiles.getObject(node, PRODUCT, Product.class);
        // TODO: como hacemos con los tank?
        // TODO: se asigna aca el estado de la orden? o lo hacemos en el business cuando lo vamos a guardar, igual con la fecha de guardado?

        order.setId(idOrder);
        order.setExpectedChargeDate(expectedChargeDate);
        order.setPreset(preset);

        order.setDriver(driver);
        order.setTruck(truck);
        order.setClient(client);
        order.setProduct(product);

        return order;
    }
}
