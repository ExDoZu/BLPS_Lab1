package com.blps.lab1.controllers;

import java.util.Date;

import com.blps.lab1.model.beans.Post;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePost {

    public ResponsePost(Post post) {
        this.id = post.getId();
        this.creationDate = post.getCreationDate();
        this.pathsToPhotos = post.getPathsToPhotos();
        this.paidUntil = post.getPaidUntil();
        this.user = new ResponseUser(post.getUser());

        this.title = post.getTitle();
        this.description = post.getDescription();
        this.price = post.getPrice();
        this.roomNumber = post.getRoomNumber();
        this.area = post.getArea();
        this.floor = post.getFloor();
        this.address = new ResponseAddress(post.getAddress());
        this.metro = new ResponseMetro(post.getMetro());
    }

    private Long id;

    private Date creationDate;

    private String[] pathsToPhotos;

    private Date paidUntil;

    private ResponseUser user;

    // Flat fields

    private String title;

    private String description;

    private Double price;

    private Integer roomNumber;

    private Double area;

    private Integer floor;

    private ResponseAddress address;

    private ResponseMetro metro;

}
