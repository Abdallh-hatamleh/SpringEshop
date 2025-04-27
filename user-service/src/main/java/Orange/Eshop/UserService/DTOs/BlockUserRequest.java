package Orange.Eshop.UserService.DTOs;

import lombok.Data;

public class BlockUserRequest {
    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail()
    {
        return this.email;
    }
    public BlockUserRequest() {

    }

}
