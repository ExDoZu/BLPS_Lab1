package com.blps.lab1.controllers.dao;

import com.blps.lab1.model.beans.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUser {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    public ResponseUser(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }
}
