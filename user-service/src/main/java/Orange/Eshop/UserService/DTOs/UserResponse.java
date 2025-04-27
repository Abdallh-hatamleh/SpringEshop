package Orange.Eshop.UserService.DTOs;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private boolean isAdmin;
    private boolean isEmailVerified;

}
