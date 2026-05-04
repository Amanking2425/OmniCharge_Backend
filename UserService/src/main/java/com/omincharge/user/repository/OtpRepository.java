package com.omincharge.user.repository;

import com.omincharge.user.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByEmailOrderByIdDesc(String email);

    void deleteByEmail(String email);
}

//package com.omincharge.user.repository;
//
//import com.omincharge.user.entity.Otp;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Repository
//public interface OtpRepository extends JpaRepository<Otp, Long> {
//
//    Optional<Otp> findTopByEmailOrderByIdDesc(String email);
//
//    @Transactional
//    void deleteByEmail(String email);
//}
