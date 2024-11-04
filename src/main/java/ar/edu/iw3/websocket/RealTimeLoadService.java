package ar.edu.iw3.websocket;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.LoadDataBusiness;
import ar.edu.iw3.model.business.OrderBusiness;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.business.exceptions.PasswordException;
import ar.edu.iw3.model.business.exceptions.StateException;

@Service
public class RealTimeLoadService {
    
    @Autowired
    private OrderBusiness orderBusiness;

    @Autowired
    private SimpMessagingTemplate realTimeSocket;

    @Autowired
    private LoadDataBusiness loadDataBusiness;

    private boolean isLoading = false;
    private long currentOrderId = 0;

    //este scheduler de abajo prepara 2 threads para ejecutar los 2 updaters en forma concurrente, entonces los 2 van y updatean juntitos
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void startLoading(long id, Integer password) throws BusinessException, NotFoundException, StateException, PasswordException {
        Order order;
        try{
            order = orderBusiness.find(id);
        }catch (NotFoundException e){
            throw NotFoundException.builder().message("Cannot start loading order. Order not found, id = " + id).build();
        }catch (BusinessException e){
            throw BusinessException.builder().ex(e).build();
        }

        if(order.getState() == Order.State.CHARGED){
            throw StateException.builder().message("This order was already charged, id = " + id).build();
        }else if (order.getState() == Order.State.RECEIVED){
            throw StateException.builder().message("This order is not ready to be charged, id = " + id).build();
        }else if (order.getState() == Order.State.FINAL_WEIGHING){
            throw StateException.builder().message("Cannot load. Order is done, id = " + id).build();
        }

        this.currentOrderId = id;

        boolean passwordCheck = false;

        if(password.equals(order.getPassword())){
            passwordCheck = true;
        }

        if(passwordCheck){
            //updatea las "ultima-" columnas de la orden. es entre 0 y 1, y se puede modificar el 1.
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    updateOrder();
                } catch (BusinessException e) {
                    e.printStackTrace();
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }, 0, 1, TimeUnit.SECONDS);

            //updatea las columas de la tabla LoadData, que tiene la persistencia de cargas. es entre 0 y 1, y se puede modificar el 1 (NOTA: ESTO PUEDE SUMAR DESPUES SI LO HACEMOS MODIFICABLE).
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    updateLoadData();
                } catch (BusinessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }, 0, 5, TimeUnit.SECONDS);
        }else{
            throw PasswordException.builder().message("Password is incorrect.").build();
        }
    }

    //frena la carga y cierra la orden en estado 3
    private void stopLoading(Order order) throws NotFoundException, BusinessException{
        this.isLoading = false;
        this.currentOrderId = 0;
        order.setState(Order.State.CHARGED);
        try{
            orderBusiness.update(order);
        }catch (BusinessException e){
            throw BusinessException.builder().ex(e).build();
        }



    }

    private void updateOrder() throws BusinessException, NotFoundException{
        //check de cierre de carga
        if(!isLoading || currentOrderId == 0){
            return;
        }

        //preguntar que onda esto, y si el cargado es con randoms o hay que hacer calculos
        Order order;
        try{
            order = orderBusiness.find(currentOrderId);
        }catch (NotFoundException e){
            throw NotFoundException.builder().message("Cannot start loading order. Order not found, id = " + currentOrderId).build();
        }catch (BusinessException e){
            throw BusinessException.builder().ex(e).build();
        }

        //order.setLastMass();

        realTimeSocket.convertAndSend("/topic/order", order);
    }


    private void updateLoadData() throws BusinessException, NotFoundException, FoundException{
        //check de cierre de carga
        if(!isLoading || currentOrderId == 0){
            return;
        }
        Order order;
        try{
            order = orderBusiness.find(currentOrderId);
        }catch (NotFoundException e){
            throw NotFoundException.builder().message("Cannot start loading order. Order not found, id = " + currentOrderId).build();
        }catch (BusinessException e){
            throw BusinessException.builder().ex(e).build();
        }

        LoadData loadData = new LoadData();

        loadData.setAccumulatedMass(order.getLastMass());
        loadData.setCaudal(order.getLastDensity());
        loadData.setDensity(order.getLastDensity());
        loadData.setTemperature(order.getLastTemperature());
        loadData.setTimestampLoad(order.getLastTimestamp());

        try {
            loadDataBusiness.add(loadData);
        } catch (FoundException e) {
            throw FoundException.builder().message("Cannot add load data. Load data already exists.").build();
        } catch (BusinessException e) {
            throw BusinessException.builder().ex(e).build();
        }

        realTimeSocket.convertAndSend("/topic/loadData", loadData);



    }



}

*/