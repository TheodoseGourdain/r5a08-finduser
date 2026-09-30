package r5a08.finduser.ms_user.convert;

import r5a08.finduser.ms_user.dto.CreateUserDTO;
import r5a08.finduser.ms_user.dto.UserDTO;
import r5a08.finduser.ms_user.entity.User;

public class Convert {

     public UserDTO toDTOUser(User user) {
        return new UserDTO(
                user.getId(),
                user.getPrenom(),
                user.getNom(),
                user.getEmail()
            );
    }

    public User toModelUser(UserDTO userDTO) {
        User user = new User();

        user.setId(userDTO.getId());
        user.setPrenom(userDTO.getPrenom());
        user.setNom(userDTO.getNom());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    public User toModelCreateUser(CreateUserDTO userDTO) {
        User user = new User();
        user.setPrenom(userDTO.getPrenom());
        user.setNom(userDTO.getNom());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    public UserDTO toDTOCreateUser (User user) {
        return new UserDTO(
                user.getId(),
                user.getPrenom(),
                user.getNom(),
                user.getEmail()
            );
    }
    
}
