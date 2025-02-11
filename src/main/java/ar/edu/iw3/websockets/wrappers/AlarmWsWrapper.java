package ar.edu.iw3.websockets.wrappers;

import ar.edu.iw3.model.Alarm;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AlarmWsWrapper {

    private long id;
    private long orderId;
    private Alarm.State status;
    private float temperature;
    private String observation;
    private Date dateResolved;
    private Date dateOcurrence;
    private String user;
    private float limitTemperature;

}