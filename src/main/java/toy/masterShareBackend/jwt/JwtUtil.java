package toy.masterShareBackend.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailsService userDetailsService;

    private static final int ACCESS_TOKEN_MINUTES = 60;
    private static final int REFRESH_TOKEN_MINUTES = 60 * 24;

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    public String generateAccessToken(Map<String, Object> claims) {

        claims.put("tokenType", "access");

        return generateToken(claims, ACCESS_TOKEN_MINUTES);
    }

    public String generateRefreshToken(Map<String, Object> claims) {

        claims.put("tokenType", "refresh");

        return generateToken(claims, REFRESH_TOKEN_MINUTES);
    }

    private String generateToken(Map<String, Object> claims, int min) {

        return Jwts.builder()
                .addClaims(claims)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(getSecretKey())
                .setSubject((String) claims.get("username"))
                .compact();
    }

    private SecretKey getSecretKey() {
        try {
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> validateToken(String token) {

        try {
            return getClaims(token);
        } catch (ExpiredJwtException expiredJwtException) {
            throw expiredJwtException;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Authentication getAuthentication(Map<String, Object> claims) {

        UserDetails userDetails = userDetailsService.loadUserByUsername((String) claims.get("username"));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private Map<String, Object> getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
