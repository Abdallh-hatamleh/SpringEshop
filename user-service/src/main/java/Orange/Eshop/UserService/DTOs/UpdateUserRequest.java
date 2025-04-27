package Orange.Eshop.UserService.DTOs;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserRequest {

    @Size(min = 3, max = 50)
    private String name;

    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    @Pattern(regexp = ".*\\S.*", message = "Password must not be blank")
    private String password;

    private MultipartFile image;

    public String toString() {
        return "Name : " + name + " , pass: " + password;
    }
}
