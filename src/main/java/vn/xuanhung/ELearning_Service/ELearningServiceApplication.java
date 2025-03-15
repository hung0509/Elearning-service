package vn.xuanhung.ELearning_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ELearningServiceApplication {

	public static void main(String[] args) {
		System.setProperty("aws.java.v1.printLocation", "true");
		System.setProperty("aws.java.v1.disableDeprecationAnnouncement", "true");
		SpringApplication.run(ELearningServiceApplication.class, args);
	}

}
