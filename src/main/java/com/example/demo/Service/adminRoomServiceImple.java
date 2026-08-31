package com.example.demo.Service;

import com.example.demo.Repo.AdminRoomRepo;
import com.example.demo.Repo.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class adminRoomServiceImple implements AdminRoomService {

    @Autowired
    private AdminRoomRepo adminRoomRepo;

    @Override
    public List<Room> getAllRooms() {
        return adminRoomRepo.findAll();
    }

    @Override
    public List<Room> searchById(String id) {
        if (id == null || id.isEmpty()) {
            return adminRoomRepo.findAll();
        }
        return adminRoomRepo.searchById(id);
    }
    /*@Override
    public List<Room> searchRoomByIdFuzzy(String id) {
        return adminRoomRepo.findByIdContaining(id).orElse(null);
    }*/

    @Override
    public void addNewRoom(Room newRoom) {
        try {
            // clean ItFacility
            newRoom.setItFacility(cleanItFacility(newRoom.getItFacility()));
            adminRoomRepo.save(newRoom);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add new room:" + e.getMessage());
        }
    }

    // assistant function: clean the itFacility field to ensure no extra ","
    private String cleanItFacility(String itFacility) {
        if (itFacility == null || itFacility.isEmpty()) {
            return itFacility;
        }
        // if the string ends with "," then delete it
        if (itFacility.endsWith(",")) {
            return itFacility.substring(0, itFacility.length() - 1);
        }
        return itFacility;
    }

    @Override
    public Room getRoomById(String id) {
        return adminRoomRepo.findById(id).orElse(null);
    }

    @Override
    public boolean updateRoom(String id, Room updatedRoom, String currentImageUrl) {
        Room existingRoom = adminRoomRepo.findById(id).orElse(null);
        if (existingRoom == null) return false;

        // clean the repeated IT Facility
        if (updatedRoom.getItFacility() != null) {
            String cleanedFacilities = Arrays.stream(updatedRoom.getItFacility().split(","))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.joining(","));
            updatedRoom.setItFacility(cleanedFacilities);
        }

        // deal with image logic
//        if (updatedRoom.getImgUrl() == null || updatedRoom.getImgUrl().isEmpty()) {
//
//            updatedRoom.setImgUrl(currentImageUrl);
//        }

        // update room information
        existingRoom.setItFacility(updatedRoom.getItFacility());
        existingRoom.setLocation(updatedRoom.getLocation());
        existingRoom.setNumber(updatedRoom.getNumber());
        existingRoom.setCapacity(updatedRoom.getCapacity());
        existingRoom.setAvailability(updatedRoom.getAvailability());
        existingRoom.setImgUrl(updatedRoom.getImgUrl());

        adminRoomRepo.save(updatedRoom);
        return true;
    }

    /*@Override
    public boolean updateRoom(String id, Room updatedRoom) {
        Optional<Room> optionalRoom = adminRoomRepo.findById(id);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();

            // update room information
            room.setIndex(updatedRoom.getIndex());
            room.setItFacility(updatedRoom.getItFacility());
            room.setLocation(updatedRoom.getLocation());
            room.setNumber(updatedRoom.getNumber());
            room.setImgUrl(updatedRoom.getImgUrl());
            room.setCapacity(updatedRoom.getCapacity());
            room.setAvailability(updatedRoom.getAvailability());

            adminRoomRepo.save(room); // save the updated information
        }
        return false;
    }*/

    @Override
    public void deleteRoom(String id) {
        adminRoomRepo.deleteById(id);
    }

    @Override
    public List<Room> filterRooms(String itFacility, Boolean availability, String location, Integer minCapacity, Integer maxCapacity, Boolean hasImage) {
        List<Room> allRooms = adminRoomRepo.findAll();
        List<Room> filteredRooms = new ArrayList<>();

        for (Room room : allRooms) {
            boolean match = true;

            // filter by it facility
            if (itFacility != null && !itFacility.isEmpty()) {
                String[] selectedFacilities = itFacility.split(",");
                boolean hasAllSelectedFacilities = true;
                for (String facility : selectedFacilities) {
                    if (!room.getItFacility().contains(facility.trim())) {
                        hasAllSelectedFacilities = false;
                        break;
                    }
                }
                if (!hasAllSelectedFacilities) {
                    match = false;
                }
            }

            // filter by Availability
            if (availability != null && room.getAvailability() != availability) {
                match = false;
            }

            // filter by Location
            if (location != null && !location.isEmpty()) {
                String[] selectedLocation = location.split(",");
                boolean hasAllSelectedLocation = false;
                for (String loca : selectedLocation) {
                    if (room.getLocation().equals(loca.trim())) {
                        hasAllSelectedLocation = true;
                        break;
                    }
                }
                if (!hasAllSelectedLocation) {
                    match = false;
                }
            }

            // filter by Capacity
            if (true) {
                int capacity = room.getCapacity();
                if(capacity < minCapacity || capacity > maxCapacity) {
                    match = false;
                }
            }

            // filter by image
            if (hasImage != null) {
                boolean roomHasImage = room.getImgUrl() != null && !room.getImgUrl().isEmpty();
                if (hasImage && !roomHasImage) {
                    match = false;
                } else if (!hasImage && roomHasImage) {
                    match = false;
                }
            }

            if (match) {
                filteredRooms.add(room);
            }
        }

        return filteredRooms;
    }

    public boolean isRoomIdExists(String roomId) {
        return adminRoomRepo.existsById(roomId); // assume use Spring Data JPA
    }

}
