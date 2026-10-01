package r5a08.finduser.ms_user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import r5a08.finduser.ms_user.convert.Convert;
import r5a08.finduser.ms_user.dto.CreateUserDTO;
import r5a08.finduser.ms_user.dto.UserDTO;
import r5a08.finduser.ms_user.entity.User;
import r5a08.finduser.ms_user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;




@RestController
@RequestMapping("/users")
public class UserController {

    
    private Convert convert;
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        this.convert = new Convert();
    }

    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getUsers()
                .stream()
                .map(convert::toDTOUser)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@RequestParam Long id) {
        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convert.toDTOUser(user));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(convert.toDTOCreateUser(userService.createUser(convert.toModelCreateUser(user))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> putMethodName(@PathVariable Long id, @RequestBody CreateUserDTO user) {

        User u = userService.getUser(id);

        if (user == null || id == null || u == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(convert.toDTOCreateUser(userService.updateUser(id, convert.toModelCreateUser(user))));
    }


    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}