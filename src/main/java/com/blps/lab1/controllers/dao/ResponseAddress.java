package com.blps.lab1.controllers.dao;

import com.blps.lab1.model.beans.Address;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAddress {
    private String city;
    private String street;
    private Integer houseNumber;
    private Character houseLetter;

    public ResponseAddress(Address address) {
        this.city = address.getCity();
        this.street = address.getStreet();
        this.houseNumber = address.getHouseNumber();
        this.houseLetter = address.getHouseLetter();
    }

}
