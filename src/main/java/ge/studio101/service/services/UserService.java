package ge.studio101.service.services;

import ge.studio101.service.models.UserEntity;
import ge.studio101.service.models.UserRole;
import ge.studio101.service.repositories.RoleRepository;
import ge.studio101.service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final RoleRepository roleRepository;


    public UserEntity findOrCreateUser(String googleId, String email, String name, String picture) {
        return userRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setPicture(picture);

                    if (newUser.getRole() == null) {
                        Optional<UserRole> defaultRole = roleRepository.findById(3L);
                        defaultRole.ifPresent(newUser::setRole);
                    }
                    if (newUser.getStatus() == null) {
                        newUser.setStatus("Активен");
                    }
                    if (newUser.getLastActive() == null) {
                        newUser.setLastActive(java.time.OffsetDateTime.now());
                    }

                    return userRepository.save(newUser);
                });
    }


    public byte[] getAvatar(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        byte[] imageBytes = user.getImage();
        if (imageBytes != null) {
            return imageBytes;
        }

        String url = user.getPicture();
        if (url != null && !url.isEmpty()) {
            try {
                // Download the image from the URL
                byte[] downloadedImage = restTemplate.getForObject(url, byte[].class);

                if (downloadedImage != null) {
                    // Store the image in the database
                    user.setImage(downloadedImage);
                    userRepository.save(user);

                    return downloadedImage;
                }
            } catch (Exception e) {
                // Handle exceptions (e.g., invalid URL, network issues)
                throw new RuntimeException("Failed to fetch image from URL: " + url, e);
            }
        }

        return null;
    }
}
