package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.LoadData;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

import java.util.List;

public interface ILoadDataBusiness {
    public LoadData find(long id) throws NotFoundException, BusinessException;

    public LoadData add(LoadData loadData) throws FoundException, BusinessException;

    public List<LoadData> list() throws BusinessException;
}
