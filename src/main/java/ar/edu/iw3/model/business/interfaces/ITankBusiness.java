package ar.edu.iw3.model.business.interfaces;

import ar.edu.iw3.model.Tank;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;

public interface ITankBusiness {
    public Tank find(long id) throws NotFoundException, BusinessException;

//    public Tank find(String tank) throws NotFoundException, BusinessException;

    public Tank add(Tank tank) throws BusinessException, FoundException;

    //public Tank findOrCreate(Tank tank) throws BusinessException;

    public Tank update(Tank tank) throws NotFoundException, BusinessException, FoundException;
}
