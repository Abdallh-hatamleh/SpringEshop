package Orange.Eshop.UserService.DTOs;

import lombok.Data;

@Data
public class UpdatePasswordRequest {

    private String email;
    private String resetToken;
    private String newPassword;
}
