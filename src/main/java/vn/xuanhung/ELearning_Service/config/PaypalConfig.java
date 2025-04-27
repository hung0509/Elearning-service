package vn.xuanhung.ELearning_Service.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaypalConfig {
    @Value("${paypal.client-id}")
    private String client_id;

    @Value("${paypal.client-secret}")
    private String client_secret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext createApiContext(){
        return new APIContext(client_id, client_secret,mode);
    }
}
