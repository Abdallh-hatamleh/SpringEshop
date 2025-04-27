package Orange.Eshop.UserService.DTOs;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterdEvent  implements Serializable {

    private String email;
    private String name;

    public UserRegisterdEvent(){}

    public UserRegisterdEvent(String name, String email)
    {
        this.email = email;
        this.name = name;
    }
}
