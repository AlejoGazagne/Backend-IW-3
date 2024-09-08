package ar.edu.iw3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ar.edu.iw3.model.business.IProductBusiness;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication 
@Slf4j
public class BackendApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Autowired
	private IProductBusiness productBusiness;

	@Value("${spring.profiles.active}")
	private String profile;

	@Override
	public void run(String... args) throws Exception {
		log.info("Perfil activo '{}'",profile);
//		try {
//			Product p = productBusiness.load(1);
//			p.setProduct("Leche");
//			p.setPrice(150);
//			Product p1 = productBusiness.update(p);
//			log.info(p1.toString());
//		} catch (Exception e) {
//			log.warn(e.getMessage());
//		}
	}
}
