package ru.yandex.practicum.paymentservice.config.security;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.paymentservice.entity.User;
import ru.yandex.practicum.paymentservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements ApplicationRunner {

    @Value("${market.default-user.username:${MARKET_DEFAULT_USER_USERNAME:buyer}}")
    private String defaultUsername;

    @Value("${market.default-user.password:${MARKET_DEFAULT_USER_PASSWORD:buyer}}")
    private String defaultPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        User user = userRepository.findByUsername(defaultUsername).orElse(null);
        if (user == null) {
            userRepository.save(User.builder()
                    .username(defaultUsername)
                    .password(passwordEncoder.encode(defaultPassword))
                    .enabled(true)
                    .build());
            return;
        }

        // If the user was created by DB migrations with a temporary password, replace it with a BCrypt one.
        if (user.getPassword() == null || user.getPassword().isBlank() || "TEMP".equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
