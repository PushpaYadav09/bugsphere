package com.bugsphere.bugsphere.security;

import jakarta.servlet.FilterChain;         // lets us pass the request to the next filter in the chain
import jakarta.servlet.ServletException;    // checked exception for servlet errors
import jakarta.servlet.http.HttpServletRequest;  // the incoming HTTP request
import jakarta.servlet.http.HttpServletResponse; // the outgoing HTTP response
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;    // signals this parameter should never be null
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// ↑ Spring Security's object that represents "this user is authenticated"
import org.springframework.security.core.context.SecurityContextHolder;
// ↑ a thread-local store — holds the current user for this request
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// ↑ adds extra details like IP address to the authentication object
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
// ↑ base class that guarantees this filter runs exactly once per request (not multiple times)

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // By extending OncePerRequestFilter, Spring guarantees doFilterInternal() is called once per request

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override



    protected void doFilterInternal(

            @NonNull HttpServletRequest request,   // the incoming request (contains the token)
            @NonNull HttpServletResponse response, // the outgoing response
            @NonNull FilterChain filterChain       // the rest of the filter chain
    ) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Step 1: Read the Authorization header
        // Every authenticated request must have: Authorization: Bearer eyJhbGci...
        final String authHeader = request.getHeader("Authorization");

        // Step 2: If there's no Authorization header, or it doesn't start with "Bearer ",
        // this request has no token — skip our filter and continue the chain.
        // Spring Security will then block it if the endpoint requires authentication.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // pass to next filter
            return; // stop here, don't continue in this method
        }

        // Step 3: Extract the token — remove the "Bearer " prefix (7 characters)
        // "Bearer eyJhbGci..." → "eyJhbGci..."
        final String jwt = authHeader.substring(7);

        // Step 4: Extract the username from the token
        final String username = jwtUtil.extractUsername(jwt);

        // Step 5: If we got a username AND this request isn't already authenticated
        // (SecurityContextHolder.getContext().getAuthentication() == null means not yet authenticated)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6: Load the full user object from the database using the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Step 7: Validate the token — check username matches AND token hasn't expired
            if (jwtUtil.isTokenValid(jwt, userDetails)) {

                // Step 8: Create an authentication token
                // This is Spring Security's way of saying "this user is authenticated"
                // authorities = their role (ROLE_ADMIN or ROLE_USER)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,              // the user object
                                null,                     // credentials (null — we already verified via JWT)
                                userDetails.getAuthorities() // their roles/permissions
                        );

                // Step 9: Attach extra request details (IP, session ID) to the auth object
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 10: Store the authentication in the SecurityContext
                // This tells Spring Security: "this request belongs to this authenticated user"
                // Now @PreAuthorize and other security checks will work correctly
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 11: Pass the request to the next filter (or the controller if no more filters)
        filterChain.doFilter(request, response);
    }
}