package r5a08.finduser.ms_user.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import r5a08.finduser.ms_user.entity.User;
import r5a08.finduser.ms_user.repository.UserRepository;

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User u = userRepository.findById(id).orElse(null);

        if (u == null) {
            return null;
        }

        u.setPrenom(user.getPrenom());
        u.setNom(user.getNom());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        return userRepository.save(u);
    }

    public boolean deleteUser(Long id) {
        User u = userRepository.findById(id).orElse(null);
        if (u == null) {
            return false;
        }
        userRepository.delete(u);
        return true;
    }
}