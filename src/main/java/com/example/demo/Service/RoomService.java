package com.example.demo.Service;

import com.example.demo.Repo.Room;
import com.example.demo.Repo.RoomRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface RoomService {
    public Room findRoomByid(String id);
    public Set<Room> findAll();
}
