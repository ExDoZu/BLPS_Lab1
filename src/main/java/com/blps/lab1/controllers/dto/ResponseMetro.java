package com.blps.lab1.controllers.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMetro {
    private String name;
    private Integer branchNumber;
    private ResponseAddress address;

    public ResponseMetro(com.blps.lab1.model.beans.Metro metro) {
        this.name = metro.getName();
        this.branchNumber = metro.getBranchNumber();
        this.address = new ResponseAddress(metro.getAddress());
    }

}
