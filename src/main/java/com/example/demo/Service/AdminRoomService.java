package com.example.demo.Service;

import com.example.demo.Repo.Room;
import com.example.demo.Repo.UserView;

import java.util.List;

public interface AdminRoomService {
    List<Room> getAllRooms();
    Room getRoomById(String id);
    void addNewRoom(Room newRoom);

    boolean updateRoom(String id, Room updatedRoom, String currentImageUrl);
    void deleteRoom(String id);

    List<Room> searchById(String id);

    List<Room> filterRooms(String itFacility, Boolean availability, String location,
                           Integer minCapacity, Integer maxCapacity, Boolean hasImage);


    public boolean isRoomIdExists(String roomId);
}
