package ar.edu.iw3;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ar.edu.iw3.model.persistence.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BackendApplication  implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

	}

	//@Autowired
	//private ProductCli2Respository productCli2DAO;

	@Value("${spring.profiles.active}")
	private String profile;

	@Value("${spring.jackson.time-zone:-}")
	private String backendTimezone;

	//@Autowired
	//private IProductCli2Business productCli2Business;

	//@Autowired
	//private ProductRepository productDAO;

	// Authorizacion: Bearer ajhfkjsfhdgshdflkghslkj.jsfhglkjgsdhfljhgfdslhg.5yt983y49gy5498y

	@Override
	public void run(String... args) throws Exception {
		String tzId = backendTimezone.equals("-") ? TimeZone.getDefault().getID() : backendTimezone;
		TimeZone.setDefault(TimeZone.getTimeZone(tzId));

		log.info("-------------------------------------------------------------------------------------------------------------------");
		log.info("- Initial TimeZone: {} ({})", TimeZone.getDefault().getDisplayName(), TimeZone.getDefault().getID());
		log.info("- Perfil activo {}",profile);

		//log.info("Cantidad de productos de la categoría id=1: {}", productDAO.countProductsByCategory(1));
		//log.info("Set stock=true producto id que no existe, resultado={}", productDAO.setStock(true, 333));

		/*
		log.info("Default -------------------------------------------------------------------------------------------------------");
		productCli2Business.listExpired(new Date());

		log.info("Customizada ---------------------------------------------------------------------------------------------------");
		productCli2Business.listSlim();
		*/


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
