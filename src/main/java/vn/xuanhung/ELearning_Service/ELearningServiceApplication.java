package vn.xuanhung.ELearning_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients

public class ELearningServiceApplication {

	public static void main(String[] args) {
		System.setProperty("aws.java.v1.printLocation", "true");
		System.setProperty("aws.java.v1.disableDeprecationAnnouncement", "true");
		SpringApplication.run(ELearningServiceApplication.class, args);
	}

}
