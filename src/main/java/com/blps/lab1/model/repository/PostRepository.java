package com.blps.lab1.model.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blps.lab1.model.beans.Post;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

        @Query("select p from Post p " +
                        "where " +
                        "(:city is null or p.address.city = :city)" +
                        "and (:street is null or p.address.street = :street) " +
                        "and (:hn is null or p.address.houseNumber = :hn) " +
                        "and (:hl is null or p.address.houseLetter = :hl) " +
                        "and (:min_area is null or p.area >= :min_area) " +
                        "and (:max_area is null or p.area <= :max_area) " +
                        "and (:min_price is null or p.price >= :min_price) " +
                        "and (:max_price is null or p.price <= :max_price) " +
                        "and (:room_number is null or p.roomNumber = :room_number) " +
                        "and (:min_floor is null or p.floor >= :min_floor) " +
                        "and (:max_floor is null or p.floor <= :max_floor) " +
                        "and (:station_name is null or p.metro.name = :station_name) " +
                        "and (:branch_number is null or p.metro.branchNumber = :branch_number)")
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

        List<Post> findByArchivedAndApproved(Boolean archived, Boolean approved);
}