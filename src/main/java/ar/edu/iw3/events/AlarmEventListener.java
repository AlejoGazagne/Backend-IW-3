package ar.edu.iw3.events;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.websockets.wrappers.AlarmWsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate alarmWs;

    private void handleTemperatureExceeded(LoadData detail) {
        Date currentDay = new Date(System.currentTimeMillis());

        // Guardado de alerta en db
        Alarm alarm = new Alarm();
        Alarm alarmAdded = new Alarm();
        alarm.setOrder(detail.getOrder());
        alarm.setDateOccurrence(currentDay);
        alarm.setTemperature(detail.getTemperature());
        alarm.setStatus(Alarm.State.PENDING);

        try {
            System.out.println("sexo33");
            System.out.println(alarm);
            alarmAdded = alarmBusiness.add(alarm);
        } catch (BusinessException | FoundException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Temperature exceeded: " + detail.getTemperature());

        AlarmWsWrapper alarmWsWrapper = new AlarmWsWrapper();
        alarmWsWrapper.setId(alarmAdded.getId());
        alarmWsWrapper.setTemperature(alarmAdded.getTemperature());
        alarmWsWrapper.setOrderId(alarmAdded.getOrder().getId());
        alarmWsWrapper.setDateOcurrence(alarmAdded.getDateOcurrence());
        alarmWsWrapper.setStatus(alarmAdded.getStatus());
        alarmWsWrapper.setObservation(alarmAdded.getDescription() != null ? alarmAdded.getDescription() : null);
        alarmWsWrapper.setUser(alarmAdded.getUser() != null && alarmAdded.getUser().getUsername() != null ? alarmAdded.getUser().getUsername() : null);

        String topic = "topic/alarms/order/"+detail.getOrder().getId();
        alarmWs.convertAndSend(topic, alarmWsWrapper);


        //esto envia el mail
        String subject = "Temperatura Excedida Orden Nro " + detail.getOrder().getId();
        String message = String.format(
                """
                        ALERTA: Temperatura Excedida en la Orden Nro %s

                        Detalles de la Alerta:
                        ---------------------------------
                        Orden ID: %s
                        Fecha/Hora del Evento: %s
                        Temperatura Registrada: %.2f °C
                        Masa Acumulada: %.2f kg
                        Densidad: %.2f kg/m³
                        Caudal: %.2f Kg/h
                        ---------------------------------

                        Descripción: La temperatura del combustible ha superado el umbral establecido. \
                        Por favor, revise esta alerta lo antes posible para evitar inconvenientes.

                        Atentamente,
                        Sistema de Monitoreo de Carga""",
                detail.getOrder().getId(),
                detail.getOrder().getId(),
                alarm.getDateOcurrence(),
                detail.getTemperature(),
                detail.getAccumulatedMass(),
                detail.getDensity(),
                detail.getCaudal()
        );
        String to = "arhetonto@gmail.com";

        try {
            sendMailMessage(to, subject, message);
        } catch (BusinessException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("PROBANDO MAIL");
        System.out.println(message);
    }
    
    @Autowired 
    private JavaMailSender javaMailSender;

    private void sendMailMessage(String to, String subject, String message) throws BusinessException{
        String from = "gasmonitor@gmail.com";
        System.out.println("a dios le pido que el mail ande");
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(message);
            javaMailSender.send(msg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).message(e.getMessage()).build();
        }
    }
}