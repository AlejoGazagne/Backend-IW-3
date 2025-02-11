package ar.edu.iw3.schedule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.websockets.wrappers.AlarmWsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@EnableAsync
@Slf4j
public class Scheduler {

    @Autowired
    private IAlarmBusiness alarmBusiness;

    @Autowired
    private SimpMessagingTemplate wSock;

    // Recordatorio de alarmas sin aceptar para clientes de la aplicacion front
    @Scheduled(fixedDelay = 10, initialDelay = 1, timeUnit = TimeUnit.SECONDS)
    public void alarmReminder() {

        try {
            System.out.println("------------------------------------------ Probando");
            List<Alarm> alarms = alarmBusiness.pendingAlarms();
            for (Alarm alarm : alarms) {

                AlarmWsWrapper alarmWsWrapper = getAlarmWsWrapper(alarm);

                try {
                    wSock.convertAndSend("/topic/alarms/reminders", alarmWsWrapper);
                } catch (Exception e) {
                    log.error("Failed to send alert notification for alarm id={}", alarm.getId(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error processing alarms", e);
        }

    }

    private static AlarmWsWrapper getAlarmWsWrapper(Alarm alarm) {
        AlarmWsWrapper alarmWsWrapper = new AlarmWsWrapper();
        alarmWsWrapper.setId(alarm.getId());
        alarmWsWrapper.setOrderId(alarm.getOrder().getId());
        alarmWsWrapper.setStatus(alarm.getStatus());
        alarmWsWrapper.setTemperature(alarm.getTemperature());
        alarmWsWrapper.setDateOcurrence(alarm.getDateOcurrence());
        alarmWsWrapper.setObservation(alarm.getDescription() != null ? alarm.getDescription() : null);
        alarmWsWrapper.setUser(
                alarm.getUser() != null && alarm.getUser().getUsername() != null
                        ? alarm.getUser().getUsername()
                        : null
        );
        return alarmWsWrapper;
    }
}
