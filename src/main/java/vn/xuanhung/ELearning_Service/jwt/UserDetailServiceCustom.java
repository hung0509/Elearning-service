package vn.xuanhung.ELearning_Service.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.entity.Account;
import vn.xuanhung.ELearning_Service.repository.AccountRepository;

import java.util.Collections;

@Slf4j
@Service
public class UserDetailServiceCustom implements UserDetailsService {
    @Autowired
    private AccountRepository  accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);

        if(account != null){
            return UserDetailCustom.builder()
                    .userId(account.getUserId())
                    .username(account.getUsername())
                    .password(account.getPassword())
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .build();
        }else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
