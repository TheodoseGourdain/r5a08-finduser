package r5a08.finduser.ms_user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import r5a08.finduser.ms_user.convert.Convert;
import r5a08.finduser.ms_user.dto.CreateUserDTO;
import r5a08.finduser.ms_user.dto.UserDTO;
import r5a08.finduser.ms_user.entity.User;
import r5a08.finduser.ms_user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Convert convert;
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        this.convert = new Convert();
    }

    /** GET /users */
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getUsers()
                .stream()
                .map(convert::toDTOUser)
                .toList();
    }

    /** GET /users/{id} : 404 si l'utilisateur n'existe pas */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convert.toDTOUser(user));
    }

    /** POST /users : 201 Created */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO user) {
        User created = userService.createUser(convert.toModelCreateUser(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(convert.toDTOCreateUser(created));
    }

    /** PUT /users/{id} : 404 si l'utilisateur n'existe pas */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody CreateUserDTO user) {
        if (userService.getUser(id) == null) {
            return ResponseEntity.notFound().build();
        }
        User updated = userService.updateUser(id, convert.toModelCreateUser(user));
        return ResponseEntity.ok(convert.toDTOCreateUser(updated));
    }

    /** DELETE /users/{id} : 204, ou 404 si l'utilisateur n'existe pas */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.deleteUser(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}