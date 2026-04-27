package com.nookx.api.security;

import com.nookx.api.domain.User;
import com.nookx.api.repository.ProfileRepository;
import com.nookx.api.repository.UserRepository;
import com.nookx.api.service.UserBuilderService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Ensures that any authenticated application user has a Profile.
 */
@Component
public class EnsureUserProfileFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(EnsureUserProfileFilter.class);

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserBuilderService userBuilderService;

    public EnsureUserProfileFilter(
        UserRepository userRepository,
        ProfileRepository profileRepository,
        UserBuilderService userBuilderService
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.userBuilderService = userBuilderService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (
            authentication == null ||
            !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken ||
            !StringUtils.hasText(authentication.getName())
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<User> userOptional = userRepository.findOneWithAuthoritiesByLogin(authentication.getName().toLowerCase());
        if (userOptional.isPresent()) {
            ensureProfileExists(userOptional.orElse(null));
        }

        filterChain.doFilter(request, response);
    }

    private void ensureProfileExists(User user) {
        if (profileRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }

        try {
            LOG.info("Profile not found for user {}, creating default profile", user.getLogin());
            userBuilderService.createUserProfile(user);
        } catch (DataIntegrityViolationException e) {
            LOG.debug("Profile already created concurrently for user {}", user.getLogin());
        }
    }
}
