package ar.edu.iw3.controllers;

import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.URL_WEIGHING)
@Tag(description = "API del servicio de pesaje", name = "Weighing")
public class WeighingController extends BaseRestController {
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private IOrderBusiness orderBusiness;


}
