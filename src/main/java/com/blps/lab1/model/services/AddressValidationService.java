package com.blps.lab1.model.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressValidationService {

    public boolean checkAddressParams(String city, String street, Integer houseNumber, Character houseLetter) {

        if (city == null || city.length() == 0) {
            return false;
        }
        if (street != null && street.length() == 0) {
            return false;
        }
        if (houseNumber != null && houseNumber <= 0) {
            return false;
        }
        if (houseLetter != null && houseLetter < 'A') {
            return false;
        }

        return true;
    }
}
