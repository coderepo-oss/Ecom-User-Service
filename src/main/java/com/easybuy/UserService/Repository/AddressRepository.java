package com.easybuy.UserService.Repository;

import com.easybuy.UserService.Entity.Address;
import com.easybuy.UserService.Entity.UserSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    long countByUser(UserSignup user);

    List<Address> findByUser(UserSignup user);

    Optional<Address> findById(Long addressId);

    boolean existsById(Long addressId);
}