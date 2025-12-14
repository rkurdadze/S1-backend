package ge.studio101.service.controllers;

import ge.studio101.service.models.UserEntity;
import ge.studio101.service.services.JwtService;
import ge.studio101.service.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        try {
            DecodedJWT decodedJWT = JWT.decode(token);

            String googleId = decodedJWT.getSubject();
            String email = decodedJWT.getClaim("email").asString();
            String name = decodedJWT.getClaim("name").asString();
            String picture = decodedJWT.getClaim("picture").asString();

            if (picture == null || picture.isEmpty()) {
                picture = "https://studio101.ge/assets/imgs/default-avatar.png"; // Опционально, заглушка
            }

            UserEntity user = userService.findOrCreateUser(googleId, email, name, picture);
            String jwtToken = jwtService.generateToken(user);

            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "token", jwtToken,
                    "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAvatar(@PathVariable("id") Long id) {
        try {
            byte[] imageBytes = userService.getAvatar(id);

            if (imageBytes == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Avatar with id " + id + " not found");
            }

            // Set headers for JPEG response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageBytes.length);

            // Return the image as a JPEG file
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Avatar with id " + id + " not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch avatar: " + e.getMessage());
        }
    }
}

