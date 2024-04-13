package com.blps.lab1.controllers;

import com.blps.lab1.model.beans.Post;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class ReceivePost {

    private Long id;

    private String[] pathsToPhotos;

    // Flat fields

    private String title;

    private String description;

    private Double price;

    private Integer roomNumber;

    private Double area;

    private Integer floor;

    private Long addressId;

    private Long metroId;

    @Override
    public String toString() {

        return "Title: " + title + "\n" +
                "Description: " + description + "\n" +
                "Price: " + price + "\n" +
                "Room number: " + roomNumber + "\n" +
                "Area: " + area + "\n" +
                "Floor: " + floor + "\n" +
                "Address id: " + addressId + "\n" +
                "Metro id: " + metroId + "\n" +
                "Paths to photos: " + pathsToPhotos + "\n";
    }

    // No Foreign Key - No Metro and no Address fields
    public Post toPostNoFK() {
        Post post = new Post();
        post.setPathsToPhotos(pathsToPhotos);
        post.setTitle(title);
        post.setDescription(description);
        post.setPrice(price);
        post.setRoomNumber(roomNumber);
        post.setArea(area);
        post.setFloor(floor);
        return post;
    }

    // No Foreign Key - No Metro and no Address fields
    public Post toPostNoFKwithID() {
        Post post = toPostNoFK();
        post.setId(id);
        return post;
    }

}
