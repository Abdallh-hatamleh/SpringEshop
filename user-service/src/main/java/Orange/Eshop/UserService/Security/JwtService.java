package Orange.Eshop.UserService.Security;

import Orange.Eshop.UserService.Configuration.JwtProperties;
import Orange.Eshop.UserService.Entities.User;
import Orange.Eshop.UserService.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    private final SecretKey resetSecretKey = Keys.hmacShaKeyFor("idkwhattouseforthissecret1231235123".getBytes());

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    private final UserRepository userRepository;

    public String generateResetToken(String email) {

        log.info("email before token gen " + email);
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "password_reset")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(resetSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateResetToken(String token, String expectedEmail) {
        log.info("Validating token against expected email: {}", expectedEmail);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(resetSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!"password_reset".equals(claims.get("type"))) {
                log.info("Invalid token type");
                return false;
            }

            String subject = claims.getSubject();
            log.info("Extracted subject: [{}]", subject);

            return expectedEmail.equals(subject.replace("\"", "")); // In case it's quoted like \"email\"
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    public JwtService(JwtProperties jwtProperties, UserRepository repository)
    {
        userRepository = repository;
        this.jwtProperties = jwtProperties;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String,Object> extraClaims, String username){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String username) {
        return generateToken(Map.of(),username);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public String extractUsername(String token) {

        return extractClaim(token,Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, String username) {
        final String extactedUsername = extractUsername(token);


        return  (extactedUsername.equals(username)) && !isTokenExpired(token);
    }

    public void blacklistToken(String token)
    {
        long expiry = getTokenExpiry(token);
        blacklist.put(token,expiry);
    }

    public long getTokenExpiry(String token) {
        return Jwts.parser().setSigningKey(getSignInKey())
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().getTime();
    }

    public boolean isBlacklisted(String token)
    {
        return blacklist.containsKey(token);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // every 10 minutes
    public void cleanUpExpiredTokens() {
        long now = System.currentTimeMillis();

        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);

        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("Expired tokens cleaned from blacklist at " + nowTime);
    }
}
