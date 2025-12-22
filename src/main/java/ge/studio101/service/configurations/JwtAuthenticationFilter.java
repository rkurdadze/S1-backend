package ge.studio101.service.configurations;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ge.studio101.service.models.UserEntity;
import ge.studio101.service.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final Algorithm algorithm;

    public JwtAuthenticationFilter(UserRepository userRepository, String secretKey) {
        this.userRepository = userRepository;
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            String subject = jwt.getSubject();
            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<UserEntity> user = userRepository.findByGoogleId(subject);
                user.ifPresent(this::setAuthentication);
            }
        } catch (JWTVerificationException ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(UserEntity user) {
        String role = user.getRole() != null ? user.getRole().getName() : "User";
        String authorityName = "ROLE_" + role.toUpperCase(Locale.ROOT).replace(" ", "_");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(authorityName))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
