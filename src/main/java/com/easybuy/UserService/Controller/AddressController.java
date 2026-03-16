package com.easybuy.UserService.Controller;

import com.easybuy.UserService.Annotation.AuthorizeUser;
import com.easybuy.UserService.Entity.Address;
import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Service.AddressService;
import com.easybuy.UserService.Service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @PostMapping("/{id}/address")
    @AuthorizeUser(pathVariable = "id")
    public ResponseEntity<Address> addAddress(
            @PathVariable("id") Long userId,
            @Valid @RequestBody Address address) {
        log.info("Add address request for user id: {}", userId);

        UserSignup user = userService.getUserById(userId);
        Address savedAddress = addressService.addAddress(user, address);

        log.info("Address added successfully for user id: {}", userId);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/addresses")
    @AuthorizeUser(pathVariable = "id")
    public ResponseEntity<List<Address>> getUserAddresses(
            @PathVariable("id") Long userId) {

        log.info("Get addresses request for user id: {}", userId);

        UserSignup user = userService.getUserById(userId);
        List<Address> addresses = addressService.getUserAddresses(user);

        log.info("Addresses fetched successfully for user id: {}", userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<Address> getAddressById(
            @PathVariable("addressId") Long addressId) {

        log.info("Get address request for address id: {}", addressId);
        Address address = addressService.getAddressById(addressId);

        return ResponseEntity.ok(address);
    }

    @PutMapping("/{id}/address/{addressId}")
    @AuthorizeUser(pathVariable = "id")
    public ResponseEntity<Address> updateAddress(
            @PathVariable("id") Long userId,
            @PathVariable("addressId") Long addressId,
            @Valid @RequestBody Address updatedAddress) {
        log.info("Update address request for address id: {}", addressId);

        Address address = addressService.updateAddress(addressId, updatedAddress);
        log.info("Address updated successfully for address id: {}", addressId);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{id}/address/{addressId}")
    @AuthorizeUser(pathVariable = "id")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable("id") Long userId,
            @PathVariable("addressId") Long addressId) {

        log.info("Delete address request for address id: {}", addressId);

        addressService.deleteAddress(addressId);
        log.info("Address deleted successfully for address id: {}", addressId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/address/{addressId}/primary")
    @AuthorizeUser(pathVariable = "id")
    public ResponseEntity<Address> setPrimaryAddress(
            @PathVariable("id") Long userId,
            @PathVariable("addressId") Long addressId) {
        log.info("Set primary address request for address id: {}", addressId);

        UserSignup user = userService.getUserById(userId);
        addressService.setPrimaryAddress(user, addressId);

        Address address = addressService.getAddressById(addressId);
        log.info("Primary address set successfully for user id: {}", userId);
        return ResponseEntity.ok(address);
    }
}