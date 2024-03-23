package com.blps.lab1.model.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.blps.lab1.model.beans.Address;
import com.blps.lab1.model.beans.Metro;

@Service
@RequiredArgsConstructor
public class MetroValidationService {

    public boolean checkMetroAddress(Metro metro, Address postAddress) {
        if (metro.getAddress().getCity().equals(postAddress.getCity())) {
            return true;
        }
        return false;
    }
}
