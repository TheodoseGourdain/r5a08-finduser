package r5a08.finduser.ms_user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import r5a08.finduser.ms_user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByEmail(String email);
}
