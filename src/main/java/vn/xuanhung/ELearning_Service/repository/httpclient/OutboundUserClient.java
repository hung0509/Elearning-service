package vn.xuanhung.ELearning_Service.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.xuanhung.ELearning_Service.dto.response.OutboundUserInfoResponse;


@FeignClient(name = "outbound-user", url = "https://www.googleapis.com")
public interface OutboundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutboundUserInfoResponse getUserInfo(@RequestParam("alt") String alt,
                                         @RequestParam("access_token")String accessToken);

}
