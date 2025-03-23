package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.repository.CertificateRepository;
import vn.xuanhung.ELearning_Service.service.CertificateService;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ICertificateService implements CertificateService {
    CertificateRepository certificateRepository;
    ModelMapper modelMapper;

}
