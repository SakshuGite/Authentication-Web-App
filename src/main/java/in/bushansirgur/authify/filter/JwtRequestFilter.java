package in.bushansirgur.authify.filter;

import in.bushansirgur.authify.service.AppUserDetailService;
import in.bushansirgur.authify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AppUserDetailService appUserDetailService;
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of("/login", "/register", "/send-reset-otp", "/reset-password", "/logout");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String Path = request.getServletPath();
        if (PUBLIC_URLS.contains(Path)) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = null;
        String email = null;
        final String authoriztionHeader = request.getHeader("Authorization");
        if(authoriztionHeader != null && authoriztionHeader.startsWith("Bearer")){
            jwt = authoriztionHeader.substring(7);
        }

        if(jwt == null){
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
               for(Cookie cookie:cookies){
                   if("jwt".equals(cookie.getName())){
                       jwt = cookie.getValue();
                       break;
                   }
               }
            }
        }
        if(jwt != null){
            email = jwtUtil.extractEmail(jwt);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = appUserDetailService.loadUserByUsername(email);
                if(jwtUtil.validateToken(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }





        filterChain.doFilter(request, response);
    }
}
