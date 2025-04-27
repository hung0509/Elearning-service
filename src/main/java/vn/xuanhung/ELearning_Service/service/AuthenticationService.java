package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.dto.request.AuthenticationRequest;
import vn.xuanhung.ELearning_Service.dto.request.IntrospectRequest;
import vn.xuanhung.ELearning_Service.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    public AuthenticationResponse authenticate(AuthenticationRequest req);

    public String logout(IntrospectRequest req);

    public AuthenticationResponse refresh(IntrospectRequest req);

    public AuthenticationResponse outboundAuthentication(String code);
}
