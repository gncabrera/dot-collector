package com.nookx.api.security;

import com.nookx.api.config.ApplicationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates internal scraper calls with a static API key.
 */
@Component
public class ScraperApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String SCRAPER_API_KEY_HEADER = "X-Api-Key";

    private static final String INGEST_PATH_PREFIX = "/api/admin/ingest";

    private final ApplicationProperties applicationProperties;

    public ScraperApiKeyAuthenticationFilter(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith(INGEST_PATH_PREFIX);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String expectedApiKey = applicationProperties.getScraper().getApiKey();
        String incomingApiKey = request.getHeader(SCRAPER_API_KEY_HEADER);

        if (StringUtils.hasText(expectedApiKey) && expectedApiKey.equals(incomingApiKey)) {
            String principalLogin = applicationProperties.getScraper().getPrincipalLogin();
            User principal = new User(
                principalLogin,
                "",
                List.of(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN), new SimpleGrantedAuthority(AuthoritiesConstants.USER))
            );

            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                principal,
                incomingApiKey,
                principal.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
