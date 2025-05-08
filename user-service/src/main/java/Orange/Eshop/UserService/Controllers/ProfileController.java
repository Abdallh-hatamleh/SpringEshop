package Orange.Eshop.UserService.Controllers;

import Orange.Eshop.UserService.DTOs.BlockUserRequest;
import Orange.Eshop.UserService.DTOs.UpdateUserRequest;
import Orange.Eshop.UserService.DTOs.UserResponse;
import Orange.Eshop.UserService.config.security.CustomUserDetails;
import Orange.Eshop.UserService.config.security.JwtService;
import Orange.Eshop.UserService.Services.AuthService;
//import jakarta.annotation.Resource;
//import jakarta.validation.Path;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal UserDetails userDetails) {

        log.info("controller reached");
        UserResponse userResponse = authService.getUser(userDetails.getUsername());

        return ResponseEntity.ok(userResponse);

    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @Valid @ModelAttribute UpdateUserRequest updateUserRequest)
    {
        return ResponseEntity.ok(authService.updateUser(userDetails.getId(), updateUserRequest));
    }



    @GetMapping("/pic")
    public ResponseEntity<Resource> getProfilePicture(@AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        Path file = Paths.get("uploads/").resolve(userDetails.getProfilePicture());
        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authhHeader)
    {
        String token = authhHeader.replace("Bearer ", "");
        jwtService.blacklistToken(token);
        return  ResponseEntity.ok("Logged out");
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> listUsers(@AuthenticationPrincipal CustomUserDetails userDetails,@PageableDefault(size = 10, sort = "createdAt") Pageable pageable) throws AccessDeniedException {
        if(userDetails.isAdmin()) {
            log.info("here");
            List<UserResponse> users = authService.getUsers(pageable);

            return ResponseEntity.ok(users);
        }
        else {
          throw new AccessDeniedException("Unauothrized");
        }
    }

    @PostMapping(value = "/block", consumes = "application/json")
    public ResponseEntity<String> blockUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody BlockUserRequest request) throws AccessDeniedException
    {
        log.info(request.getEmail());
        log.info(userDetails.isAdmin());
        if(userDetails.isAdmin()){
            authService.blockUser(request.getEmail());
            return ResponseEntity.ok(request.getEmail() + " Blocked successfully");
        }
        else {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    @PostMapping("/unblock")
    public ResponseEntity<String> unBlockUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody BlockUserRequest request) throws AccessDeniedException
    {
        if(userDetails.isAdmin()){
            authService.unBlockUser(request.getEmail());
            return ResponseEntity.ok(request.getEmail() + " Unblocked successfully");
        }
        else {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
