package Orange.Eshop.UserService.Services;

import Orange.Eshop.UserService.DTOs.RegisterRequest;
import Orange.Eshop.UserService.DTOs.UpdateUserRequest;
import Orange.Eshop.UserService.DTOs.UserRegisterdEvent;
import Orange.Eshop.UserService.DTOs.UserResponse;
import Orange.Eshop.UserService.Entities.User;
import Orange.Eshop.UserService.Mapper.UserMapper;
import Orange.Eshop.UserService.Repositories.UserRepository;
import Orange.Eshop.UserService.Utils.FileHandling;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final FileHandling fileHandling;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserEventPublisher userEventPublisher;

    public void updatePassword(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new UsernameNotFoundException("No User with that Email");
        }
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void register(RegisterRequest request)
    {
        if(userRepository.existsByEmail(request.getEmail()))
        {
            throw new RuntimeException("Email Already Registered!!");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);
        user.setAdmin(false);
        userRepository.save(user);

        UserRegisterdEvent event = new UserRegisterdEvent(request.getName(),request.getEmail());
        userEventPublisher.publishUserRegisteredEvent(event);
    }

    public UserResponse getUser(String email) {
        UserResponse userResponse;
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email" + email));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        log.info(request);
        if(request.getName() != null) {
            user.setName(request.getName());
        }

        if(request.getPassword() != null) {
            String pass = passwordEncoder.encode(request.getPassword());
            user.setPassword(pass);
        }

        if(request.getImage() != null && !request.getImage().isEmpty()) {
            String filename = fileHandling.saveFile(request.getImage());
            user.setProfilePicture(filename);
        }
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).stream().map(userMapper::toUserResponse).toList();
    }

    public void blockUser(String email){
//        log.info("service layer " + email);
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        log.info("Authentication: {}", auth);
//        log.info("Principal: {}", auth.getPrincipal());
//        log.info("Is authenticated: {}", auth.isAuthenticated());
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email" + email));
        user.setBlocked(true);
        userRepository.save(user);
        log.info(user.getEmail(), " Blocked");
    }
    public void unBlockUser(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        user.setBlocked(false);
        userRepository.save(user);
        log.info(user.getEmail(), " Unblocked");
    }
}
