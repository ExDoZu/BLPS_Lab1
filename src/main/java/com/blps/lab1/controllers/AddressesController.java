package com.blps.lab1.controllers;

import java.util.HashMap;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blps.lab1.model.beans.Address;
import com.blps.lab1.model.beans.Metro;
import com.blps.lab1.model.repository.AddressRepository;
import com.blps.lab1.model.repository.MetroRepository;
import com.blps.lab1.model.services.AddressValidationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AddressesController {

    private final AddressRepository addressRepository;
    private final MetroRepository metroRepository;

    private final AddressValidationService addressValidationService;

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses(
            String city,
            String street,
            Integer houseNumber,
            Character houseLetter) {

        if (!addressValidationService.checkAddressParams(city, street, houseNumber, houseLetter)) {
            return ResponseEntity.badRequest().build();
        }

        List<Address> addresses = addressRepository.findByMany(city, street, houseNumber, houseLetter);
        if (addresses.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        List<Metro> metros = metroRepository.findByAddressСity(addresses.get(0).getCity());

        var response = new HashMap<String, Object>();
        response.put("addresses", addresses);
        response.put("metros", metros);

        return ResponseEntity.ok().body(response);
    }

}
