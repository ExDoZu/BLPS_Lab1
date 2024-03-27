package com.blps.lab1.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.blps.lab1.model.beans.Address;
import com.blps.lab1.model.beans.Metro;
import com.blps.lab1.model.beans.Post;
import com.blps.lab1.model.beans.User;
import com.blps.lab1.model.repository.AddressRepository;
import com.blps.lab1.model.repository.MetroRepository;
import com.blps.lab1.model.repository.PostRepository;
import com.blps.lab1.model.repository.UserRepository;
import com.blps.lab1.model.services.AddressValidationService;
import com.blps.lab1.model.services.MetroValidationService;
import com.blps.lab1.model.services.PostValidationService;

import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {

    private final PostRepository postRepository;
    private final AddressRepository addressRepository;
    private final MetroRepository metroRepository;
    private final UserRepository userRepository;
    private final MetroValidationService metroValidationService;
    private final AddressValidationService addressValidationService;
    private final PostValidationService postValidationService;

    @DeleteMapping("/post")
    public ResponseEntity<?> archivePost(@RequestParam Map<String, String> params,
            @RequestHeader Map<String, String> headers) {

        String postIdStr = params.get("postId");
        if (postIdStr == null || !postIdStr.matches("\\d+")) {
            return ResponseEntity.badRequest().body("Invalid post id");
        }

        Long postId = Long.parseLong(postIdStr);

        Post post = postRepository.findById(postId).get();

        String token = headers.get("authorization");
        String phone;
        try {
            phone = token.substring(0, token.indexOf(":"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");

        }
        User me = userRepository.findByPhoneNumber(phone);
        if (me == null) {
            return ResponseEntity.badRequest().body("No such user");
        }
        if (me.getId() != post.getUser().getId()) {
            return ResponseEntity.badRequest().body("Not your post");
        }

        post.setArchived(true);
        postRepository.save(post);

        return ResponseEntity.ok().build();

    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses(@RequestParam Map<String, String> params) {

        if (!addressValidationService.checkAddressParams(params)) {
            if (params.get("city") == null || params.get("city").length() == 0)
                return ResponseEntity.badRequest().body("City is empty");

            return ResponseEntity.badRequest().build();
        }
        String city = params.get("city"), street = params.get("street");
        Integer houseNumber = params.get("houseNumber") == null ? null : Integer.parseInt(params.get("houseNumber"));
        Character houseLetter = params.get("houseLetter") == null ? null : params.get("houseLetter").charAt(0);

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

    final double PRICE_PER_DAY = 100;

    @Column(nullable = false)
    @PostMapping("/payment")
    public ResponseEntity<?> getMethodName(@RequestBody Map<String, Object> body,
            @RequestHeader Map<String, String> headers) {

        Long postId;
        String payUntilStr;
        try {
            postId = (Long) body.get("postId");
            payUntilStr = (String) body.get("payUntil");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        if (payUntilStr == null || !payUntilStr.matches("\\d{2}.\\d{2}.\\d{4}")) {
            return ResponseEntity.badRequest().body("Invalid date format");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date;
        try {
            date = formatter.parse(payUntilStr);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Invalid date");
        }

        // get current date
        Date currentDate = Date.from(java.time.Instant.now());
        // count days between current date and payUntil date
        long diff = date.getTime() - currentDate.getTime();
        log.info("diff: " + diff);
        long days = diff / (24 * 60 * 60 * 1000);
        double price = days * PRICE_PER_DAY;

        Post post = postRepository.findById(postId).get();

        log.info(post.toString());

        User user = post.getUser();

        String token = headers.get("authorization");
        String phone;

        try {
            phone = token.substring(0, token.indexOf(":"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");

        }
        User me = userRepository.findByPhoneNumber(phone);
        if (me == null) {
            return ResponseEntity.badRequest().body("No such user");
        }
        log.info(me.toString());
        if (me.getId() != user.getId()) {
            return ResponseEntity.badRequest().body("Not your post");
        }

        if (user.getBalance() < price) {
            return new ResponseEntity<>("Not enough money", org.springframework.http.HttpStatus.PAYMENT_REQUIRED);
        }
        post.setPaidUntil(date);
        user.setBalance(user.getBalance() - price);
        userRepository.save(user);
        postRepository.save(post);
        var response = new HashMap<String, Double>();
        response.put("price", price);
        response.put("balance", user.getBalance());
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/post")
    public ResponseEntity<?> setPost(@RequestBody ReceivePost entity,
            @RequestHeader Map<String, String> headers) {

        String phone;
        try {

            String token = headers.get("authorization");
            phone = token.substring(0, token.indexOf(":"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        User user = userRepository.findByPhoneNumber(phone);
        Address address = addressRepository.findById(entity.getAddressId()).get();
        Metro metro = metroRepository.findById(entity.getMetroId()).get();
        Post post = entity.toPostNoFK();

        // Validation of metro
        if (!metroValidationService.checkMetroAddress(metro, address)) {
            return ResponseEntity.badRequest().body("Invalid metro address");
        }

        post.setUser(user);
        post.setAddress(address);
        post.setMetro(metro);

        // add additional fields
        post.setCreationDate(Date.from(java.time.Instant.now()));
        post.setArchived(false);

        if (!postValidationService.checkPost(post)) {
            log.info(post.toString());
            return ResponseEntity.badRequest().body("Invalid post");
        }

        // Saving post to database
        try {
            postRepository.save(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPosts(@RequestParam Map<String, String> params) {

        String city, street, stationName;
        Character houseLetter;
        Double minArea, maxArea, minPrice, maxPrice;
        Integer houseNumber, roomNumber, minFloor, maxFloor, branchNumber;
        // Parsing of URL parameters
        try {
            city = params.get("city");
            street = params.get("street");
            houseNumber = params.get("houseNumber") == null ? null : Integer.parseInt(params.get("houseNumber"));
            houseLetter = params.get("houseLetter") == null ? null : params.get("houseLetter").charAt(0);
            minArea = params.get("minArea") == null ? null : Double.parseDouble(params.get("minArea"));
            maxArea = params.get("maxArea") == null ? null : Double.parseDouble(params.get("maxArea"));
            minPrice = params.get("minPrice") == null ? null : Double.parseDouble(params.get("minPrice"));
            maxPrice = params.get("maxPrice") == null ? null : Double.parseDouble(params.get("maxPrice"));
            roomNumber = params.get("roomNumber") == null ? null : Integer.parseInt(params.get("roomNumber"));
            minFloor = params.get("minFloor") == null ? null : Integer.parseInt(params.get("minFloor"));
            maxFloor = params.get("maxFloor") == null ? null : Integer.parseInt(params.get("maxFloor"));
            stationName = params.get("stationName");
            branchNumber = params.get("branchNumber") == null ? null : Integer.parseInt(params.get("branchNumber"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        // Getting posts from database

        List<Post> posts = postRepository.findByMany(city, street, houseNumber, houseLetter, minArea, maxArea, minPrice,
                maxPrice, roomNumber, minFloor, maxFloor, stationName, branchNumber);

        // Converting to response format
        List<ResponsePost> responsePosts = new java.util.ArrayList<>();
        for (Post post : posts) {
            if (post.getArchived())
                continue;
            if (post.getPaidUntil() == null || post.getPaidUntil().before(Date.from(java.time.Instant.now())))
                continue;

            responsePosts.add(new ResponsePost(post));
        }

        return ResponseEntity.ok().body(responsePosts);
    }

}
