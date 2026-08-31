package com.example.demo.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminRoomRepo  extends JpaRepository<Room, String> {
    List<Room> findByLocation(String location);
    List<Room> findByNumber(String number);
    //java.util.Optional<List<Room>> findByIdContaining(String id);
    @Query("SELECT r FROM Room r WHERE r.id LIKE %:id%")
    List<Room> searchById(@Param("id") String id);


}
