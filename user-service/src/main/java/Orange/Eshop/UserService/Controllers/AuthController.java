package Orange.Eshop.UserService.Controllers;

import Orange.Eshop.UserService.DTOs.LoginRequest;
import Orange.Eshop.UserService.DTOs.LoginResponse;
import Orange.Eshop.UserService.DTOs.RegisterRequest;
import Orange.Eshop.UserService.Entities.User;
import Orange.Eshop.UserService.Security.CustomUserDetails;
import Orange.Eshop.UserService.Services.AuthService;
import Orange.Eshop.UserService.Security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@Log4j2
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
     private final AuthService authService;

     @Autowired
     private AuthenticationManager authenticationManager;

     @Autowired
     private JwtService jwtService;

     @PostMapping("/register")
     public ResponseEntity<String> register(@RequestBody RegisterRequest request)
     {
         authService.register(request);
         return ResponseEntity.ok("User Registerd Successfully!");
     }

     @PostMapping("/login")
     public ResponseEntity<?> login(@RequestBody LoginRequest request)
     {
//         try {
             Authentication authentication = authenticationManager.authenticate(
                     new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
             );
             CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
             User user = userDetails.getUser();
            if(user.isBlocked()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are blocked by an admin");
            }
             Map<String,Object> extraClaims = new HashMap<>();
             extraClaims.put("isAdmin", user.isAdmin());
             extraClaims.put("userId", user.getId());
             extraClaims.put("name",user.getName());
             String jwt = jwtService.generateToken(extraClaims,user.getEmail());
//MSDN
             LoginResponse loginResponse = new LoginResponse(jwt);

             return ResponseEntity.ok(loginResponse);
//         }
         //ADD ERROR HANDLING
//         catch (AuthenticationException e) {
//             log.info(e.getMessage());
//             return ResponseEntity.status(404).build();
//         }
     }
}
