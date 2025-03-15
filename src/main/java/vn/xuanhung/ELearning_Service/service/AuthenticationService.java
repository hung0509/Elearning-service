package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.dto.request.AuthenticationRequest;
import vn.xuanhung.ELearning_Service.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    public AuthenticationResponse authenticate(AuthenticationRequest req);
}
