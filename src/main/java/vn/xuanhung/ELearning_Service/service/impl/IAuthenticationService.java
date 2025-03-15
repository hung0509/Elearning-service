package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.dto.request.AuthenticationRequest;
import vn.xuanhung.ELearning_Service.dto.response.AuthenticationResponse;
import vn.xuanhung.ELearning_Service.entity.Account;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.jwt.JwtUtil;
import vn.xuanhung.ELearning_Service.repository.AccountRepository;
import vn.xuanhung.ELearning_Service.service.AuthenticationService;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IAuthenticationService implements AuthenticationService {
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    AccountRepository accountRepository;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (BadCredentialsException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        log.info("User Detail: " + SecurityContextHolder.getContext().getAuthentication());
        Account account = accountRepository.findByUsername(req.getUsername());

        if (account != null) {
            String token = this.jwtUtil.generateToken(account);
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }
}
