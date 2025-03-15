package vn.xuanhung.ELearning_Service.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.jwt.JwtUtil;
import vn.xuanhung.ELearning_Service.jwt.UserDetailCustom;
import vn.xuanhung.ELearning_Service.jwt.UserDetailServiceCustom;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final UserDetailServiceCustom userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("---Filter JWT authentication---");
        String origin = request.getHeader("Origin");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        //Các endpoint không cần ddi qua
        if (request.getMethod().equalsIgnoreCase("POST")) {
            for (String publicUrl : AppConstant.URL_PUBLIC) {
                if (request.getRequestURI().equalsIgnoreCase("/elearning-service" + publicUrl)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        log.info("***JWT filter service. JWT authentication provider***");
        final var authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ") ) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetailCustom userDetails = (UserDetailCustom) this.userDetailsService.loadUserByUsername(username);

            if (this.jwtUtil.validateToken(jwt, userDetails)) {
                final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }



}
