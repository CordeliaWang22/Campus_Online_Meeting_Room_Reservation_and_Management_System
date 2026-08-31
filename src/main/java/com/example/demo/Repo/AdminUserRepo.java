package com.example.demo.Repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminUserRepo extends JpaRepository<User, Integer> {
    UserView findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    UserView findUserViewById(@Param("id") int id);

    List<UserView> findByStatus(String status);

    List<UserView> findByBookingTimesGreaterThanEqualAndBookingTimesLessThanEqual(int gBookingTimes, int lBookingTimes);

    List<UserView> findByStatusAndBookingTimesGreaterThanEqualAndBookingTimesLessThanEqual(String status, int bookingTimesIsGreaterThan, int bookingTimesIsLessThan);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword%")
    List<UserView> searchByName(String keyword);

    @Query("SELECT u FROM User u WHERE u.phoneNum LIKE %:phone%")
    List<UserView> searchByPhone(@Param("phone") String phone);

    @Query("SELECT u FROM User u WHERE u.avatar IS NULL")
    List<UserView> findUsersWithoutAvatar();

    @Query("SELECT u FROM User u")
    List<UserView> findAllWithoutPassword();

    @Query("""
    SELECT u FROM User u 
    WHERE u.name LIKE %:keyword% 
       OR u.email LIKE %:keyword% 
       OR u.phoneNum LIKE %:keyword%
    """)
    List<UserView> searchByKeywordAcrossFields(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("""
    UPDATE User u 
    SET u.status = CASE 
                      WHEN u.status = 'lock' THEN 'unlock'
                      WHEN u.status = 'unlock' THEN 'lock'
                      ELSE u.status
                  END 
    WHERE u.id = :id
    """)
    int toggleUserStatusById(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.bookingTimes = 0")
    void setAllBookingTimesToZero();

}
