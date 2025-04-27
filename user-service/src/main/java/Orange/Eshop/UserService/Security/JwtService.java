package Orange.Eshop.UserService.Security;

import Orange.Eshop.UserService.Configuration.JwtProperties;
import Orange.Eshop.UserService.Entities.User;
import Orange.Eshop.UserService.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    private final UserRepository userRepository;

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
