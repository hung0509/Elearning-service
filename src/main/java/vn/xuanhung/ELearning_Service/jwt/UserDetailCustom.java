package vn.xuanhung.ELearning_Service.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
@Builder
public class UserDetailCustom implements UserDetails {
    private String username;
    private String password;
    private int userId;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailCustom(String username, String password, int userId, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.authorities = authorities;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.getUsername();
    }


}
