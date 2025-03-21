package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.dto.request.CertificateRequest;
import vn.xuanhung.ELearning_Service.dto.response.CertificateResponse;
import vn.xuanhung.ELearning_Service.repository.CertificateRepository;
import vn.xuanhung.ELearning_Service.service.CertificateService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICertificateService implements CertificateService {
    CertificateRepository certificateRepository;
    ModelMapper modelMapper;

}
