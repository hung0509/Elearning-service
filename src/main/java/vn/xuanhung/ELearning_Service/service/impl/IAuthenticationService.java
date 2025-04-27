package vn.xuanhung.ELearning_Service.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.DateHelper;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.*;
import vn.xuanhung.ELearning_Service.dto.response.AuthenticationResponse;
import vn.xuanhung.ELearning_Service.entity.Account;
import vn.xuanhung.ELearning_Service.entity.InvalidatedToken;
import vn.xuanhung.ELearning_Service.entity.Role;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.jwt.JwtUtil;
import vn.xuanhung.ELearning_Service.repository.AccountRepository;
import vn.xuanhung.ELearning_Service.repository.InvalidatedTokenRepository;
import vn.xuanhung.ELearning_Service.repository.RoleReposiroty;
import vn.xuanhung.ELearning_Service.repository.UserInfoRepository;
import vn.xuanhung.ELearning_Service.repository.httpclient.OutboundIdentityClient;
import vn.xuanhung.ELearning_Service.repository.httpclient.OutboundUserClient;
import vn.xuanhung.ELearning_Service.service.AuthenticationService;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IAuthenticationService implements AuthenticationService {
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    AccountRepository accountRepository;
    UserInfoRepository userInfoRepository;
    RoleReposiroty roleReposiroty;
    InvalidatedTokenRepository invalidatedTokenRepository;
    MailService mailService;
    KafkaTemplate<String, Object> kafkaTemplate;

    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;
    PasswordEncoder passwordEncoder;

    String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String LOWER = "abcdefghijklmnopqrstuvwxyz";
    String DIGITS = "0123456789";
    String SPECIAL = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";
    String ALL_CHARACRTER = UPPER + LOWER + DIGITS + SPECIAL;

    @NonFinal
    @Value("${oauth2.client-id}")
    private String CLIENT_ID;

    @NonFinal
    @Value("${oauth2.client-secret}")
    private String CLIENT_SECRET;

    @NonFinal
    @Value("${oauth2.redirect_uri}")
    private String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public AuthenticationResponse outboundAuthentication(String code){
        String decodedCode = "";
        try {
            decodedCode = java.net.URLDecoder.decode(code, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new AppException(ErrorCode.DECODE_NOT_AVAILABLE);
        }
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(decodedCode)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        log.info("TOKEN RESPONSE {}", response);

        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        var user = userInfoRepository.findByEmail(userInfo.getEmail());

        Account account = null;
        if(Objects.isNull(user)) { //Neeus chua ton tai thi them user nay vao he thong
            Role role = roleReposiroty.findById("USER")
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXIST));
            String password = generatePassword();

            user = UserInfo.builder()
                    .email(userInfo.getEmail())
                    .firstName(userInfo.getFamilyName())
                    .lastName(userInfo.getGivenName())
                    .build();
            user = userInfoRepository.save(user);

            account = new Account();
            account.setRole(role);
            account.setPassword(passwordEncoder.encode(password));
            account.setIsActive("N");
            account.setUserId(user.getId());
            account = accountRepository.save(account);

            //Tien hanh gui mail
            String message = "<p>Below is the password we reset: " + password + "</p>";

            MailContentRequest mailContentRequest = MailContentRequest.builder()
                    .to(user.getLastName() + " " + user.getFirstName())
                    .title("Welcome new members")
                    .userId(user.getId())
                    .build();
            String fromMail = mailService.formGetActiveAccount(mailContentRequest);
            MailRequest mailRequest = MailRequest.builder()
                    .toEmail(user.getEmail())
                    .subject("Confirm account activation")
                    .htmlContent(fromMail)
                    .build();

            try {
                kafkaTemplate.send(AppConstant.Topic.EMAIL_TOPIC, mailRequest).get();
                log.info("Kafka send");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            account = accountRepository.findByUserId(user.getId());
        }

        String token = this.jwtUtil.generateToken(account);

        return AuthenticationResponse.builder()
                .token(token)
                .build();

    }

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

    private String generatePassword(){
        SecureRandom RANDOM = new SecureRandom();

        char[] password = new char[8];

        password[0] = UPPER.charAt(RANDOM.nextInt(UPPER.length()));
        password[1] = LOWER.charAt(RANDOM.nextInt(LOWER.length()));
        password[2] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        password[3] = SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length()));

        for(int i = 4; i < 8; i++){
            password[i] = ALL_CHARACRTER.charAt(RANDOM.nextInt(ALL_CHARACRTER.length()));
        }

        for (int i = 7; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }

        return new String(password);
    }
}
