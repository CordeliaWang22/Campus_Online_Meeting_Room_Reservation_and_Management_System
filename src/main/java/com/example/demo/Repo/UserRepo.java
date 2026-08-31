package com.example.demo.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    User findByName(String name);
    User findByEmail(String email);
    Optional<User> findByIdEquals(Integer id);
    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    int updatePasswordById(@Param("id") Integer id, @Param("password") String password);

    // Change username
    @Modifying
    @Query("UPDATE User u SET u.name = :name WHERE u.id = :id")
    int updateNameById(@Param("id") Integer id, @Param("name") String name);

    // Change avatar
    @Modifying
    @Query("UPDATE User u SET u.avatar = :avatar WHERE u.id = :id")
    int updateAvatarById(@Param("id") Integer id, @Param("avatar") String avatar);


    @Modifying
    @Query("UPDATE User u SET u.bookingTimes = :bookingTimes WHERE u.id = :id")
    int updateBookingTimesById(@Param("id") Integer id, @Param("bookingTimes") int bookingTimes);


}
