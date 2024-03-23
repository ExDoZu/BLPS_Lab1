package com.blps.lab1.model.services;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressValidationService {

    public boolean checkAddressParams(Map<String, String> params) {

        if (params.get("city") == null || params.get("city").length() == 0) {
            return false;
        }
        if (params.get("street") != null && params.get("street").length() == 0) {
            return false;
        }
        if (params.get("houseNumber") != null && !params.get("houseNumber").matches("\\d+")) {
            return false;
        }
        if (params.get("houseLetter") != null && params.get("houseLetter").length() != 1) {
            return false;
        }

        return true;
    }
}
