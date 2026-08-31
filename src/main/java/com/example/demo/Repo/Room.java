package com.example.demo.Repo;

import jakarta.persistence.*;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "it_facility")
    private String itFacility;

    @Column(name = "location")
    private String location;

    @Column(name = "number")
    private String number;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "availability")
    private Boolean availability;

    public Room() {}

    public Room(Integer index, String itFacility, String location, String number, String id, String imgUrl, Integer capacity, Boolean availability) {
        this.index = index;
        this.itFacility = itFacility;
        this.location = location;
        this.number = number;
        this.id = id;
        this.imgUrl = imgUrl;
        this.capacity = capacity;
        this.availability = availability;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getItFacility() {
        return itFacility;
    }

    public void setItFacility(String itFacility) {
        this.itFacility = itFacility;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
    public void printRoomInfo() {
        System.out.println("Room ID: " + id);
        System.out.println("Location: " + location);
        System.out.println("Room Number: " + number);
        System.out.println("Facilities: " + itFacility);
        System.out.println("Image URL: " + imgUrl);
        System.out.println("Capacity: " + capacity);
        System.out.println("Availability: " + (availability ? "Available" : "Not Available"));
    }
}
