package ar.edu.iw3.events;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;


@Slf4j
@Component
public class AlarmEventListener implements ApplicationListener<AlarmEvent> {

    @Autowired
    private IAlarmBusiness alarmBusiness;

    @Override
    public void onApplicationEvent(AlarmEvent event) {
        if (event.getTypeEvent().equals(AlarmEvent.TypeEvent.TEMPERATURE_EXCEEDED) && event.getSource() instanceof LoadData) {
            handleTemperatureExceeded((LoadData) event.getSource());
        }
    }

    private void handleTemperatureExceeded(LoadData detail) {
        Date currentDay = new Date(System.currentTimeMillis());

        // Guardado de alerta en db
        Alarm alarm = new Alarm();
        alarm.setOrder(detail.getOrder());
        alarm.setDateOcurrence(currentDay);
        alarm.setTemperature(detail.getTemperature());
        alarm.setStatus(Alarm.State.PENDING);

        try {
            alarm = alarmBusiness.add(alarm);
        } catch (BusinessException | FoundException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Temperature exceeded: " + detail.getTemperature());
    }
}