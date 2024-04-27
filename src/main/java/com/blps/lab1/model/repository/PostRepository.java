package com.blps.lab1.model.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blps.lab1.model.beans.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
        @Query("select p from Post p " +
                        "inner join fetch p.address a " +
                        "left join fetch p.metro m " +
                        "inner join fetch m.address ma" +
                        "inner join fetch p.user u " +
                        "where " +
                        "(:city is null or a.city = :city) " +
                        "and (:street is null or a.street = :street) " +
                        "and (:hn is null or a.houseNumber = :hn) " +
                        "and (:hl is null or a.houseLetter = :hl) " +
                        "and (:min_area is null or p.area >= :min_area) " +
                        "and (:max_area is null or p.area <= :max_area) " +
                        "and (:min_price is null or p.price >= :min_price) " +
                        "and (:max_price is null or p.price <= :max_price) " +
                        "and (:room_number is null or p.roomNumber = :room_number) " +
                        "and (:min_floor is null or p.floor >= :min_floor) " +
                        "and (:max_floor is null or p.floor <= :max_floor) " +
                        "and (:station_name is null or m.name = :station_name) " +
                        "and (:branch_number is null or m.branchNumber = :branch_number)" +

                        "and (p.paidUntil > CURRENT_TIMESTAMP) and (p.archived=false) and (p.approved=true)")
        Page<Post> findByMany(
                        @Param("city") String city,
                        @Param("street") String street,
                        @Param("hn") Integer houseNumber,
                        @Param("hl") Character houseLetter,
                        @Param("min_area") Double minArea,
                        @Param("max_area") Double maxArea,
                        @Param("min_price") Double minPrice,
                        @Param("max_price") Double maxPrice,
                        @Param("room_number") Integer roomNumber,
                        @Param("min_floor") Integer minFloor,
                        @Param("max_floor") Integer maxFloor,
                        @Param("station_name") String stationName,
                        @Param("branch_number") Integer branchNumber,
                        Pageable pageable);

        @EntityGraph(attributePaths = { "user", "address", "metro" })
        Page<Post> findByUser_PhoneNumber(String phoneNumber, Pageable pageable);

        @EntityGraph(attributePaths = { "user", "address", "metro" })
        Page<Post> findByArchivedAndApproved(Boolean archived, Boolean approved, Pageable pageable);
}