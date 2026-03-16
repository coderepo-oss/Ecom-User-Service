package com.easybuy.UserService.Repository;
import com.easybuy.UserService.Entity.UserSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserSignup,Long>{

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<UserSignup> findByEmail(String email);

}
