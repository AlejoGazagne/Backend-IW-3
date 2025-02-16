package ar.edu.iw3.websockets.wrappers;

import ar.edu.iw3.model.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class LoadDataWsWrapper {

    private long id;
    private float accumulatedMass;
    private float density;
    private float temperature;
    private float caudal;
    private Date timestampLoad;
    private long orderId;
    private String externalId;
}