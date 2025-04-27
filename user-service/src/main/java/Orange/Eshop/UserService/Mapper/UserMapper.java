package Orange.Eshop.UserService.Mapper;

import Orange.Eshop.UserService.DTOs.RegisterRequest;
import Orange.Eshop.UserService.DTOs.UserResponse;
import Orange.Eshop.UserService.Entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

//    @Mapping(target = "profilePictureUrl", ignore = true)
    public UserResponse toUserResponse(User user);

    public User toUser(RegisterRequest registerRequest);

}
