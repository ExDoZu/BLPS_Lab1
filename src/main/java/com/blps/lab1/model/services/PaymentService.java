package com.blps.lab1.model.services;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.blps.lab1.exceptions.AccessDeniedException;
import com.blps.lab1.exceptions.PaymentException;
import com.blps.lab1.model.beans.Post;
import com.blps.lab1.model.beans.User;
import com.blps.lab1.model.repository.PostRepository;
import com.blps.lab1.model.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public class PayResult {
        private double balance;
        private double price;

        public PayResult(double balance, double price) {
            this.balance = balance;
            this.price = price;
        }

        public double getBalance() {
            return balance;
        }

        public double getPrice() {
            return price;
        }

    }

    final double PRICE_PER_DAY = 100;

    public PayResult pay(Date date, Long postId, User me) throws AccessDeniedException, PaymentException {
        // get current date
        Date currentDate = Date.from(java.time.Instant.now());
        // count days between current date and payUntil date
        long diff = date.getTime() - currentDate.getTime();

        long days = diff / (24 * 60 * 60 * 1000);
        double price = days * PRICE_PER_DAY;

        Post post = postRepository.findById(postId).orElse(null);

        User user = post.getUser();

        if (me.getId() != user.getId()) {
            throw new AccessDeniedException("Access denied. Not your post");
        }

        if (user.getBalance() < price) {
            throw new PaymentException("Not enough money");
        }

        post.setPaidUntil(date);
        user.setBalance(user.getBalance() - price);
        userRepository.save(user);
        postRepository.save(post);
        return new PayResult(user.getBalance(), price);
    }

}
