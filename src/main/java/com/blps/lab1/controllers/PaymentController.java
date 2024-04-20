package com.blps.lab1.controllers;

import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blps.lab1.controllers.dto.ReceivePayment;
import com.blps.lab1.exceptions.AccessDeniedException;

import com.blps.lab1.model.services.PaymentService;
import com.blps.lab1.model.services.PaymentService.PayResult;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<?> getMethodName(

            @RequestBody ReceivePayment paymentBody,
            @RequestHeader("authorization") String token) {

        String phone;
        try {
            phone = token.substring(0, token.indexOf(":"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");

        }

        PayResult payResult;
        try {
            payResult = paymentService.pay(paymentBody.getPaidUntil(), paymentBody.getPostId(), phone);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        var response = new HashMap<String, Double>();
        response.put("price", payResult.getPrice());
        response.put("balance", payResult.getBalance());
        return ResponseEntity.ok(response);

    }

}
