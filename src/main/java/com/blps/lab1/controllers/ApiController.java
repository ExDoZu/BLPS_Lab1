package com.blps.lab1.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blps.lab1.exceptions.NotFoundException;
import com.blps.lab1.model.beans.Address;
import com.blps.lab1.model.beans.Metro;
import com.blps.lab1.model.beans.Post;
import com.blps.lab1.model.beans.User;
import com.blps.lab1.model.repository.AddressRepository;
import com.blps.lab1.model.repository.MetroRepository;
import com.blps.lab1.model.repository.PostRepository;
import com.blps.lab1.model.repository.UserRepository;
import com.blps.lab1.model.services.AddressValidationService;
import com.blps.lab1.model.services.PaymentService;
import com.blps.lab1.model.services.PostService;
import com.blps.lab1.model.services.PaymentService.PayResult;
import com.blps.lab1.model.services.PostService.GetResult;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {

    private final PostRepository postRepository;
    private final AddressRepository addressRepository;
    private final MetroRepository metroRepository;
    private final UserRepository userRepository;

    private final AddressValidationService addressValidationService;

    private final PostService postService;
    private final PaymentService paymentService;

    @DeleteMapping("/posts")
    public ResponseEntity<?> archivePost(@RequestParam Map<String, String> params,
            @RequestHeader Map<String, String> headers) {

        String postIdStr = params.get("postId");
        if (postIdStr == null || !postIdStr.matches("\\d+")) {
            return ResponseEntity.badRequest().body("Invalid post id");
        }

        long postId = Long.parseLong(postIdStr);

        String token = headers.get("authorization");
        String phone;
        try {
            phone = token.substring(0, token.indexOf(":"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");

        }
        User me = userRepository.findByPhoneNumber(phone);
        if (me == null) {
            return ResponseEntity.badRequest().body("Invalid token. No such user");
        }

        // model part
        try {
            postService.delete(postId, me);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // end model part

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

        // model part

        List<Address> addresses = addressRepository.findByMany(city, street, houseNumber, houseLetter);
        if (addresses.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        List<Metro> metros = metroRepository.findByAddressСity(addresses.get(0).getCity());

        // end model part

        var response = new HashMap<String, Object>();
        response.put("addresses", addresses);
        response.put("metros", metros);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> getMethodName(@RequestBody Map<String, Object> body,
            @RequestHeader Map<String, String> headers) {

        long postId;
        String payUntilStr;
        try {
            postId = ((Number) body.get("postId")).longValue();
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

        // model part
        PayResult payResult = paymentService.pay(date, postId, me);
        // end model part

        var response = new HashMap<String, Double>();
        response.put("price", payResult.getPrice());
        response.put("balance", payResult.getBalance());
        return ResponseEntity.ok(response);

    }

    @PostMapping("/posts")
    public ResponseEntity<?> setPost(@RequestBody ReceivePost entity,
            @RequestHeader Map<String, String> headers) {

        String phone;
        try {

            String token = headers.get("authorization");
            phone = token.substring(0, token.indexOf(":"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        Long addressID = entity.getAddressId();
        Long metroID = entity.getMetroId();
        Post post = entity.toPostNoFK();

        // model part
        try {
            postService.post(phone, addressID, metroID, post);
        } catch (NotFoundException e) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // end model part

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/posts")
    public ResponseEntity<?> updatePost(@RequestBody ReceivePost entity,
            @RequestHeader Map<String, String> headers) {

        String phone;
        try {
            String token = headers.get("authorization");
            phone = token.substring(0, token.indexOf(":"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        Post post = entity.toPostNoFKwithID();
        Long addressID = entity.getAddressId();
        Long metroID = entity.getMetroId();
        // model part
        try {
            postService.post(phone, addressID, metroID, post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // end model part

        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId, @RequestHeader Map<String, String> headers) {
        // model part
        Post post = postRepository.findById(postId).orElse(null);
        // end model part
        if (post == null)
            return ResponseEntity.notFound().build();
        boolean archived = post.getArchived();
        Boolean approved = post.getApproved();
        Date paidUntil = post.getPaidUntil();

        String token = headers.get("authorization");
        if (token != null) {
            String phone = token.split(":")[0];
            User me = userRepository.findByPhoneNumber(phone);
            if (me == null) {
                return ResponseEntity.badRequest().body("Invalid token. No such user");
            }
            if (post.getUser().getId() == me.getId())
                return ResponseEntity.ok().body(new ResponsePost(post));
        }

        if (!archived && approved && paidUntil != null && paidUntil.after(Date.from(java.time.Instant.now())))
            return ResponseEntity.ok().body(new ResponsePost(post));
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(@RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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

        // model part

        GetResult getResult = postService.get(page, size, city, street, houseNumber, houseLetter, minArea, maxArea,
                minPrice, maxPrice, roomNumber, minFloor, maxFloor, stationName, branchNumber);
        // end model part

        List<ResponseSimplePost> responsePosts = new ArrayList<>();
        for (Post post : getResult.getPosts()) {
            responsePosts.add(new ResponseSimplePost(post));
        }
        if (page >= getResult.getTotalPages())
            return ResponseEntity.badRequest().body("No such page");
        var response = new HashMap<String, Object>();
        response.put("posts", responsePosts);
        response.put("totalPages", getResult.getTotalPages());
        response.put("currentPage", page);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/moderation")
    public ResponseEntity<?> getModerationPosts(@RequestHeader Map<String, String> headers) {

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

        if (!me.isModerator()) {
            return ResponseEntity.badRequest().body("Not a moderator");
        }

        // model part
        List<Post> posts = postRepository.findByArchivedAndApproved(false, null);
        // end model part

        List<ResponsePost> responsePosts = new java.util.ArrayList<>();
        for (Post post : posts) {
            responsePosts.add(new ResponsePost(post));
        }
        return ResponseEntity.ok().body(responsePosts);

    }

    @PostMapping("/moderation")
    public ResponseEntity<?> setModeration(@RequestBody Map<String, Object> body,
            @RequestHeader Map<String, String> headers) {

        long postId;
        boolean approved;
        try {
            postId = ((Number) body.get("postId")).longValue();
            approved = (boolean) body.get("approved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request" + e.getMessage());
        }

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

        if (!me.isModerator()) {
            return ResponseEntity.badRequest().body("Not a moderator");
        }

        // model part
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null)
            return ResponseEntity.badRequest().body("No post with this id");
        post.setApproved(approved);
        // set not archived if approved
        if (approved) {
            post.setArchived(false);
        }
        postRepository.save(post);
        // end model part

        return ResponseEntity.ok().build();
    }

}
