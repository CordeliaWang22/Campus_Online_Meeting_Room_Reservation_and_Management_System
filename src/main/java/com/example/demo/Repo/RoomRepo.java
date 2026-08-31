package com.example.demo.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Room,String> {
    public Room findByid(String id);
    public  boolean existsById(String id);
}
