package com.example.demo.Service;

import com.example.demo.Repo.Room;
import com.example.demo.Repo.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoomServiceImple implements RoomService {
    @Autowired
    private RoomRepo roomRepo;

    @Override
    public Room findRoomByid(String Room_id) {
        return roomRepo.findByid(Room_id);
    }

    @Override
    public Set<Room> findAll() {
        List<Room> all = roomRepo.findAll();
        Set<Room> allSet = new HashSet<>();
        allSet.addAll(all);
        return allSet;
    }

}
