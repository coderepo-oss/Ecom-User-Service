package com.easybuy.UserService.Service;

import com.easybuy.UserService.Entity.Address;
import com.easybuy.UserService.Entity.UserSignup;
import com.easybuy.UserService.Exception.AddressException;
import com.easybuy.UserService.Repository.AddressRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    private static final int MAX_ADDRESSES = 10;

    public Address addAddress(UserSignup user, @Valid Address address) {
        long addressCount = addressRepository.countByUser(user);

        if (addressCount >= MAX_ADDRESSES) {
            log.warn("Address limit exceeded for user: {}", user.getEmail());
            throw new AddressException(
                    "ADDRESS_LIMIT_EXCEEDED",
                    "Address limit reached. You can only have up to " + MAX_ADDRESSES + " addresses."
            );
        }

        if (isDuplicateAddress(user, address)) {
            log.warn("Duplicate address attempt for user: {}", user.getEmail());
            throw new AddressException(
                    "DUPLICATE_ADDRESS",
                    "This address already exists in your account"
            );
        }

        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        log.info("Address added successfully for user: {}", user.getEmail());

        return savedAddress;
    }

    /**
     * Check if exact same address (all fields) already exists
     */
    private boolean isDuplicateAddress(UserSignup user, Address address) {
        List<Address> existingAddresses = addressRepository.findByUser(user);

        return existingAddresses.stream()
                .anyMatch(existingAddr ->
                        existingAddr.getStreet().equalsIgnoreCase(address.getStreet()) &&
                                existingAddr.getCity().equalsIgnoreCase(address.getCity()) &&
                                existingAddr.getState().equalsIgnoreCase(address.getState()) &&
                                existingAddr.getZipCode().equalsIgnoreCase(address.getZipCode()) &&
                                existingAddr.getCountry().equalsIgnoreCase(address.getCountry())
                );
    }

    public List<Address> getUserAddresses(UserSignup user) {
        log.info("Fetching addresses for user: {}", user.getEmail());
        return addressRepository.findByUser(user);
    }

    public Address getAddressById(Long addressId) {
        log.info("Fetching address with id: {}", addressId);

        return addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.warn("Address not found with id: {}", addressId);
                    return new AddressException(
                            "ADDRESS_NOT_FOUND",
                            "Address not found with ID: " + addressId
                    );
                });
    }

    public Address updateAddress(Long addressId, @Valid Address updatedAddress) {
        log.info("Updating address with id: {}", addressId);

        Address address = getAddressById(addressId);

        if (isDuplicateAddressForUpdate(address.getUser(), address.getId(), updatedAddress)) {
            log.warn("Duplicate address detected during update");
            throw new AddressException(
                    "DUPLICATE_ADDRESS",
                    "This address already exists in your account"
            );
        }

        if (updatedAddress.getStreet() != null) {
            address.setStreet(updatedAddress.getStreet());
        }
        if (updatedAddress.getCity() != null) {
            address.setCity(updatedAddress.getCity());
        }
        if (updatedAddress.getState() != null) {
            address.setState(updatedAddress.getState());
        }
        if (updatedAddress.getZipCode() != null) {
            address.setZipCode(updatedAddress.getZipCode());
        }
        if (updatedAddress.getCountry() != null) {
            address.setCountry(updatedAddress.getCountry());
        }
        if (updatedAddress.getAddressType() != null) {
            address.setAddressType(updatedAddress.getAddressType());
        }

        Address updated = addressRepository.save(address);
        log.info("Address updated successfully with id: {}", addressId);

        return updated;
    }

    /**
     * Check for exact duplicate when updating (exclude current address)
     */
    private boolean isDuplicateAddressForUpdate(UserSignup user, Long currentAddressId, Address updatedAddress) {
        List<Address> existingAddresses = addressRepository.findByUser(user);

        return existingAddresses.stream()
                .filter(addr -> !addr.getId().equals(currentAddressId))  // Exclude current address
                .anyMatch(existingAddr ->
                        existingAddr.getStreet().equalsIgnoreCase(updatedAddress.getStreet()) &&
                                existingAddr.getCity().equalsIgnoreCase(updatedAddress.getCity()) &&
                                existingAddr.getState().equalsIgnoreCase(updatedAddress.getState()) &&
                                existingAddr.getZipCode().equalsIgnoreCase(updatedAddress.getZipCode()) &&
                                existingAddr.getCountry().equalsIgnoreCase(updatedAddress.getCountry())
                );
    }

    public void deleteAddress(Long addressId) {
        log.info("Deleting address with id: {}", addressId);

        if (!addressRepository.existsById(addressId)) {
            log.warn("Cannot delete - Address not found with id: {}", addressId);
            throw new AddressException(
                    "ADDRESS_NOT_FOUND",
                    "Cannot delete - Address not found with ID: " + addressId
            );
        }

        addressRepository.deleteById(addressId);
        log.info("Address deleted successfully with id: {}", addressId);
    }

    public void setPrimaryAddress(UserSignup user, Long addressId) {
        log.info("Setting primary address with id: {} for user: {}", addressId, user.getEmail());

        Address address = getAddressById(addressId);

        if (!address.getUser().getId().equals(user.getId())) {
            log.warn("Forbidden - Address {} does not belong to user: {}", addressId, user.getEmail());
            throw new AddressException(
                    "FORBIDDEN",
                    "This address does not belong to you"
            );
        }

        List<Address> addresses = addressRepository.findByUser(user);
        addresses.forEach(addr -> {
            if (!addr.getId().equals(addressId)) {
                addr.setIsPrimary(false);
                addressRepository.save(addr);
            }
        });

        address.setIsPrimary(true);
        addressRepository.save(address);

        log.info("Primary address set successfully with id: {} for user: {}", addressId, user.getEmail());
    }
}