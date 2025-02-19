package org.slackcoder.twilight.security;

import java.util.Optional;
import java.util.UUID;

import org.slackcoder.twilight.model.User;
import org.slackcoder.twilight.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            UUID uuid = UUID.fromString(userId);
            Optional<User> user = userRepository.findById(uuid);
            if (user.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.get().getUserId().toString())
                    .password(user.get().getPassword())
                    .roles(String.valueOf(user.get().getUserType()))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid UUID format");
        }
    }
}
