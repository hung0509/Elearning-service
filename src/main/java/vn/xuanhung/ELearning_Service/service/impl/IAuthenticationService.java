package vn.xuanhung.ELearning_Service.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
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
import vn.xuanhung.ELearning_Service.common.DateHelper;
import vn.xuanhung.ELearning_Service.dto.request.AuthenticationRequest;
import vn.xuanhung.ELearning_Service.dto.request.IntrospectRequest;
import vn.xuanhung.ELearning_Service.dto.response.AuthenticationResponse;
import vn.xuanhung.ELearning_Service.entity.Account;
import vn.xuanhung.ELearning_Service.entity.InvalidatedToken;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.jwt.JwtUtil;
import vn.xuanhung.ELearning_Service.repository.AccountRepository;
import vn.xuanhung.ELearning_Service.repository.InvalidatedTokenRepository;
import vn.xuanhung.ELearning_Service.service.AuthenticationService;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IAuthenticationService implements AuthenticationService {
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    AccountRepository accountRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest req) {
        log.info("***Log authentication service - authenticate account***");
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

    @Override
    public String logout(IntrospectRequest req) {
        log.info("***Log authentication service - authenticate account***");
        try {
            String jwtUUID = jwtUtil.extractUuid(req.getToken());
            Date expireDate = jwtUtil.extractExpiration(req.getToken());

            InvalidatedToken invalidatedToken = new InvalidatedToken();
            invalidatedToken.setId(jwtUUID);
            invalidatedToken.setExpireDate(expireDate);

            invalidatedTokenRepository.save(invalidatedToken);
            return "Logout successfully!";
        }
        catch(AppException ignored){
            return null;
        }
    }

    @Override
    public AuthenticationResponse refresh(IntrospectRequest req) {
        try {
            jwtUtil.verify(req.getToken(), true);   //Check

            String jwt_id = jwtUtil.extractUuid(req.getToken());
            Date localDate = jwtUtil.extractExpiration(req.getToken());
            String username = jwtUtil.extractUsername(req.getToken());

            InvalidatedToken invalidatedToken = InvalidatedToken
                    .builder()
                    .id(jwt_id)
                    .expireDate(localDate)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

           Account account = accountRepository.findByUsername(username);

            String token = jwtUtil.generateToken(account);
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();

        }catch (Exception e){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
}
