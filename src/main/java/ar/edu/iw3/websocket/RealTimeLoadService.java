package ar.edu.iw3.websocket;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.LoadDataBusiness;
import ar.edu.iw3.model.business.OrderBusiness;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

@Service
public class RealTimeLoadService {
    
    @Autowired
    private OrderBusiness orderBusiness;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private LoadDataBusiness loadDataBusiness;

    private final Random random = new Random();

    @Scheduled(fixedRate = 1000)
    public void updateOrderFields (int orderId) {
    
        Order order = null;
        try {
            order = orderBusiness.find(orderId);
        } catch (NotFoundException e) {
            // TO-DO Auto-generated catch block
            e.printStackTrace();
        } catch (BusinessException e) {
            // TO-DO Auto-generated catch block
            e.printStackTrace();
        }

        if (loadingInProgress) {
            return;
        }
        loadingInProgress = true;
        while (order.getLastMass() < order.getPreset()) {
            order.setLastMass(order.getLastMass() + random.nextFloat());
            order.setLastDensity(order.getLastDensity() + random.nextFloat());
            order.setLastCaudal(order.getLastCaudal() + random.nextFloat());
            order.setLastTemperature(order.getLastTemperature() + random.nextFloat());
            order.setLastTimestamp(Date.from(new Date().toInstant()));
            template.convertAndSend("/topic/order/" + orderId, order);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        loadingInProgress = false;
    }

    private Boolean loadingInProgress = false;

}
