package ar.edu.iw3.model.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ar.edu.iw3.model.Provider;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.exceptions.NotFoundException;
import ar.edu.iw3.model.persistence.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iw3.model.Product;
import ar.edu.iw3.model.persistence.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductBusiness implements IProductBusiness {

	// IoC
	@Autowired
	private ProductRepository productDAO;

	@Autowired
	private ProviderRepository providerDAO;
	
	@Override
	public Product load(long id) throws NotFoundException, BusinessException {
		Optional<Product> r;
		
		try {
			r = productDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if(r.isEmpty())
			throw NotFoundException.builder().message("No se encuentra el Producto id = " + id).build();
		
		return r.get();
	}
	
	@Override
	public Product load(String product) throws NotFoundException, BusinessException {
		Optional<Product> r;
		
		try {
			r = productDAO.findByProduct(product);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
		if(r.isEmpty())
			throw NotFoundException.builder().message("No se encuentra el Producto denominado " + product).build();
		
		return r.get();
	}

	@Override
	public Product add(Product product) throws FoundException, BusinessException {
		List<Provider> providers = new ArrayList<>();
		Optional<Provider> existingProvider;

		try {
			load(product.getId());
			throw FoundException.builder().message("Se encontró el producto id = " + product.getId()).build();
		} catch (NotFoundException ignored) {	}
		
		try {
			load(product.getProduct());
			throw FoundException.builder().message("Se encontró el producto " + product.getProduct()).build();
		} catch (NotFoundException ignored) {}

		for (Provider provider : product.getProviders()) {
			try{
				existingProvider = providerDAO.findById(provider.getId());
				if (existingProvider.isEmpty())
					throw NotFoundException.builder().message("[ERROR] No se encontró el producto " + product.getId()).build();
				providers.add(existingProvider.get());
			}catch(Exception e){
				log.error(e.getMessage(),e);
				throw BusinessException.builder().ex(e).build();
			}
		}

		product.setProviders(providers);
		try {
			return productDAO.save(product);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
	}
	
	@Override
	public List<Product> list() throws BusinessException {
		try {
			return productDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
	}

// 1 Arroz 189 true
// 2 Leche 50  true

// 1 Leche 190 true <-- esto no puede ocurrir!!!!!!
	//load(product.getProduct()); CHacer todo para que esto funcione!!!!

	@Override
	public Product update(Product product) throws FoundException, NotFoundException, BusinessException {
		load(product.getId());
		// load(product.getProduct());
		Optional<Product> nombreExistente;
		try {
			nombreExistente = productDAO.findByProductAndIdNot(product.getProduct(), product.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (nombreExistente.isPresent()) {
			throw FoundException.builder().message("Se encontró el Producto nombre=" + product.getProduct()).build();
		}
		try {
			return productDAO.save(product);
		} catch (Exception e) {

			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public void delete(Product product) throws NotFoundException, BusinessException {
		delete(product.getId());
	}

	@Override
	public void delete(long id) throws NotFoundException, BusinessException {
		load(id);
		try {
			productDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw BusinessException.builder().ex(e).build();
		}
	}
}
