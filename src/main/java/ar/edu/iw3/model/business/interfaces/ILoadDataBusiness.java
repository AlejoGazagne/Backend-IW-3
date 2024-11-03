package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.sql.Timestamp;
import java.util.List;

public interface ILoadDataBusiness {
    public LoadData find(long id) throws NotFoundException, BusinessException;

    public LoadData add(LoadData loadData) throws FoundException, BusinessException;

    public List<LoadData> list() throws BusinessException;

    public Order createLoadData(Timestamp currentTime, LoadData loadData, Order order) throws FoundException, BusinessException, NotFoundException;


    public List<LoadData> list(long orderId) throws BusinessException;

    public Double avgTemperature(long orderId) throws BusinessException, NotFoundException;

    public Double avgDensity(long orderId) throws BusinessException, NotFoundException;

    public Double avgCaudal(long orderId) throws BusinessException, NotFoundException;
}
