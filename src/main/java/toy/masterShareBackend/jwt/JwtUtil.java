package toy.masterShareBackend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import toy.masterShareBackend.domain.User;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    public String generateToken(User user, int min) {

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(getSecretKey())
                .setSubject(user.getUsername())
                .addClaims(user.toClaims())
                .compact();
    }

    private SecretKey getSecretKey() {
        try {
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}
