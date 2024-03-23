package com.blps.lab1.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blps.lab1.model.beans.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

        @Query("SELECT a FROM Address a WHERE " +
                        "(:city is null or a.city = :city) AND " +
                        "(:street is null or a.street = :street) AND " +
                        "(:hn is null or a.houseNumber = :hn) AND " +
                        "(:hl is null or a.houseLetter = :hl)")
        List<Address> findByMany(@Param("city") String city,
                        @Param("street") String street,
                        @Param("hn") Integer houseNumber,
                        @Param("hl") Character houseLetter);

}
