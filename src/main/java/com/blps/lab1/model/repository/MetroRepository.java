package com.blps.lab1.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blps.lab1.model.beans.Metro;
import java.util.List;

@Repository
public interface MetroRepository extends JpaRepository<Metro, Long> {
    List<Metro> findByName(String name);

    @Query("SELECT m FROM Metro m WHERE m.address.city = :city")
    List<Metro> findByAddressСity(@Param("city") String city);

    List<Metro> findByBranchNumber(Integer branchNumber);
}
