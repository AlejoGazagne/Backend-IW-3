package ar.edu.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ar.edu.iw3.controllers.Constants;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.interfaces.IAlarmBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_ALARMS)
public class AlarmRestController extends BaseRestController{
    @Autowired
    private IAlarmBusiness alarmBusiness;

    @Autowired
    private IStandartResponseBusiness response;

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingAlarms(){
        try {
            return new ResponseEntity<>(alarmBusiness.pendingAlarms(), HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
