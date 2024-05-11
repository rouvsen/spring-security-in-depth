package az.ingress.auth;

import az.ingress.model.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value(value = "${security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value(value = "${security.jwt.validity-minutes}")
    private long ACCESS_TOKEN_VALIDITY_MINUTES;

    private static final String TOKEN_HEADER = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtParser jwtParser;

    public String createToken(User user) {
        Date expirationDate = calculateExpirationDate(ACCESS_TOKEN_VALIDITY_MINUTES);
        Claims claims = buildClaims(user);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .setExpiration(expirationDate)
                .compact();
    }

    private Claims buildClaims(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        return claims;
    }

    private Date calculateExpirationDate(long validityMinutes) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + TimeUnit.MINUTES.toMillis(validityMinutes);
        return new Date(expirationTimeMillis);
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest request) {
        try {
            String token = resolveToken(request);
            if (Objects.nonNull(token)) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException exception) {
            request.setAttribute("expired", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            request.setAttribute("invalid", exception.getMessage());
            throw exception;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String requestHeader = request.getHeader(TOKEN_HEADER);
        if (Objects.nonNull(requestHeader) && requestHeader.startsWith(TOKEN_PREFIX)) {
            return requestHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception exception) {
            throw exception;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    private List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }
}
