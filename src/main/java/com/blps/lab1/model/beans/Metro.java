package com.blps.lab1.model.beans;

import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Metro {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "metro_seq")
    @SequenceGenerator(name = "metro_seq", sequenceName = "metro_id_sequence")
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Integer branchNumber;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

}
