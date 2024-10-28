package ar.edu.iw3;

import ar.edu.iw3.model.Order;
import ar.edu.iw3.model.Tank;
import ar.edu.iw3.model.Truck;
import ar.edu.iw3.model.business.exceptions.BusinessException;
import ar.edu.iw3.model.business.exceptions.FoundException;
import ar.edu.iw3.model.business.interfaces.IOrderBusiness;
import ar.edu.iw3.model.business.interfaces.ITankBusiness;
import ar.edu.iw3.model.business.interfaces.ITruckBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@Slf4j
public class BackendApplication  implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private ITruckBusiness truckBusiness;

	@Autowired
	private ITankBusiness tankBusiness;

	@Override
	public void run(String... args) throws Exception {
//		String tzId = backendTimezone.equals("-") ? TimeZone.getDefault().getID() : backendTimezone;
//		TimeZone.setDefault(TimeZone.getTimeZone(tzId));
//
//		log.info("-------------------------------------------------------------------------------------------------------------------");
//		log.info("- Initial TimeZone: {} ({})", TimeZone.getDefault().getDisplayName(), TimeZone.getDefault().getID());
//		log.info("- Perfil activo {}",profile);

		//log.info("Cantidad de productos de la categoría id=1: {}", productDAO.countProductsByCategory(1));
		//log.info("Set stock=true producto id que no existe, resultado={}", productDAO.setStock(true, 333));

		/*
		log.info("Default -------------------------------------------------------------------------------------------------------");
		productCli2Business.listExpired(new Date());

		log.info("Customizada ---------------------------------------------------------------------------------------------------");
		productCli2Business.listSlim();
		*/


//		String json = "{\"id\": 123, \"preset\": 100.0, \"expectedChargeDate\": \"2023-11-22T12:34:56\", \"driver\": {\"id\": 456, \"name\": \"alejo\", \"lastname\": \"gazagne\", \"document\": \"321321\"}}";;
//		//String json = "{\"id\": 123, \"preset\": 100.0, \"expectedChargeDate\": \"2023-11-22T12:34:56\"}";;
//		Order order = orderBusiness.addExternal(json);
		
//		Truck truck = new Truck();
//		truck.setId(4);
//		truck.setPlate("ggg123");
//		List<Tank> tanks = new ArrayList<>();
//		Tank tank = new Tank();
//		tank.setCapacity(1000);
//		tank.setTruck(truck);
//		tanks.add(tank);
//		truck.setTanks(tanks);
//		truckBusiness.add(truck);

		
		/*
		try {
			Product p=new Product();
			p.setProduct("Arroz");
			p.setPrice(156.67);
		z	productBusiness.add(p);
			Product p1=productBusiness.load(p.getId());
			Product p2=productBusiness.load(p.getProduct());
			log.info(p1.toString());
			log.info(p2.toString());
		} catch (Exception e) {
			log.warn(e.getMessage());
		}*/
		//System.out.println(productCli2DAO.findAll());

	}

}
