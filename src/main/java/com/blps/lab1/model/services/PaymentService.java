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

    public PayResult pay(Date date, Long postId, String userPhone) throws AccessDeniedException, PaymentException {
        final User me = userRepository.findByPhoneNumber(userPhone);
        if (me == null) {
            throw new AccessDeniedException("No such user");
        }

        final Date currentDateTime = Date.from(java.time.Instant.now());

        final long diff = date.getTime() - currentDateTime.getTime();
        if (diff <= 0) {
            throw new PaymentException("Invalid date");
        }
        final long days = diff / (24 * 60 * 60 * 1000) + 1;
        final double price = days * PRICE_PER_DAY;

        Post post = postRepository.findById(postId).orElse(null);

        User user = post.getUser();

        if (me.getId() != user.getId()) {
            throw new AccessDeniedException("Access denied. Not your post");
        }

        if (post.getPaidUntil() != null && !post.getPaidUntil().before(date)) {
            throw new PaymentException("Post is already paid");
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
