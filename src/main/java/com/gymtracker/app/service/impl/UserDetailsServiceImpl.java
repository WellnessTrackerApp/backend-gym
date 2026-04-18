package com.gymtracker.app.service.impl;

import com.gymtracker.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UUID uuid;

        try {
            uuid = UUID.fromString(userId);
        } catch(IllegalArgumentException e) {
            throw new UsernameNotFoundException(
                    messageSource.getMessage("username-not-found-exception.invalid-user-id-format", null, LocaleContextHolder.getLocale())
            );
        }

        return userRepository.findByIdWithoutCollections(uuid)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage("username-not-found-exception.user-not-found", new Object[] {uuid}, LocaleContextHolder.getLocale())
                ));
    }
}
