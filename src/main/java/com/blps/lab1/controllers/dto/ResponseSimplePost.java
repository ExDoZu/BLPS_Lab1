package com.blps.lab1.controllers.dto;

import com.blps.lab1.model.beans.Post;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSimplePost {

    public ResponseSimplePost(Post post) {
        this.id = post.getId();
        this.pathsToPhotos = post.getPathsToPhotos();
        this.title = post.getTitle();
        this.price = post.getPrice();
        this.roomNumber = post.getRoomNumber();
        this.area = post.getArea();
        this.floor = post.getFloor();
        this.address = new ResponseAddress(post.getAddress());
        this.metro = post.getMetro().getName() + " (" + post.getMetro().getBranchNumber().toString() + ")";

    }

    private Long id;

    private String[] pathsToPhotos;

    // Flat fields

    private String title;

    private Double price;

    private Integer roomNumber;

    private Double area;

    private Integer floor;

    private ResponseAddress address;

    private String metro;

}
