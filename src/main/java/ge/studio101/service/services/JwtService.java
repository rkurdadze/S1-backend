package ge.studio101.service.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ge.studio101.service.models.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(UserEntity user) {
        return JWT.create()
                .withSubject(user.getGoogleId())
                .withClaim("email", user.getEmail())
                .withClaim("name", user.getName())
                .withClaim("picture", user.getPicture())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 день
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String getSecretKey() {
        return secretKey;
    }
}
