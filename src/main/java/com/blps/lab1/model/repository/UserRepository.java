package com.blps.lab1.model.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.blps.lab1.model.beans.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByPhoneNumber(String phoneNumber);
}
