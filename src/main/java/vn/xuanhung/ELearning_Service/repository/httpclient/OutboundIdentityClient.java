package vn.xuanhung.ELearning_Service.repository.httpclient;

import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import vn.xuanhung.ELearning_Service.dto.request.ExchangeTokenRequest;
import vn.xuanhung.ELearning_Service.dto.response.ExchangeTokenResponse;

@FeignClient(name = "outbound-identity", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}
