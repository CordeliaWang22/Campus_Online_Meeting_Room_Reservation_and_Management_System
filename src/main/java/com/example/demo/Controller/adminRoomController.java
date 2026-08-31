package com.example.demo.Controller;

import com.example.demo.Repo.Room;
import com.example.demo.Service.AdminRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/admin/room")

public class adminRoomController {
    @Autowired
    private AdminRoomService adminRoomService;

    @Value("${room.upload.dir}")
    private String uploadDir;

    @Value("${room.access.path}")
    private String accessPath;

    // view all rooms
    @GetMapping
    public String getAllRooms(Model model) {
        List<Room> allRooms = adminRoomService.getAllRooms();
        for (int i = 0; i < allRooms.size(); i++) {
            System.out.println(allRooms.get(i).getId()+":"+allRooms.get(i).getImgUrl());
        }
        model.addAttribute("allRooms", allRooms); // add all received room to model
        return "AdminRoomManagement";
    }

    // the page of add new room
    @GetMapping("/add")
    public String showAddRoomPage() {
        return "AddNewRoom";
    }

    // add room
//    @PostMapping("/add")
//    public String addNewRoom(
//            @ModelAttribute Room newRoom,
//            @RequestParam("itFacility") List<String> itFacilities,
//            @RequestParam("imageFile") MultipartFile imageFile,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            newRoom.setItFacility(null);
//            for (int i = 0; i < itFacilities.size(); i++) {
//
//                newRoom.setItFacility(itFacilities.get(i)+",");
//            }
//
//            // file upload
//            if (!imageFile.isEmpty()) {
//                Path uploadPath = Paths.get(uploadDir);
//                Files.createDirectories(uploadPath);
//
//                String originalFilename = imageFile.getOriginalFilename();
//                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
//
//                // complete path to store file
//                Path filePath = uploadPath.resolve(uniqueFileName);
//                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//                // set visit url not file path
//                newRoom.setImgUrl("/rooms/" + uniqueFileName);
//            }
//
//            System.out.println("room:"+newRoom.getId());
//            System.out.println("url:"+newRoom.getImgUrl());
//
//
//
//            adminRoomService.addNewRoom(newRoom);
//            return "redirect:/admin/room";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
//            return "redirect:/admin/room/add";
//        }
//    }

    @PostMapping("/add")
    public String addNewRoom(
            @ModelAttribute Room newRoom,
            @RequestParam("itFacility") List<String> itFacilities,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        // deal with it facility
//            newRoom.setItFacility(String.join(",", itFacilities));
        try {
            // check if id is exist
            if (adminRoomService.isRoomIdExists(newRoom.getId())) {
                redirectAttributes.addFlashAttribute("error", "Room ID already exists.");
                return "redirect:/admin/room/add";
            }

            newRoom.setItFacility(null);
            for (int i = 0; i < itFacilities.size(); i++) {

                newRoom.setItFacility(itFacilities.get(i) + ",");
            }

            // deal with file upload
            if (!imageFile.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);

                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // file storage
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // set image url
                newRoom.setImgUrl("/rooms/" + uniqueFileName);
            }

            //add new room information to database
            adminRoomService.addNewRoom(newRoom);
            return "redirect:/admin/room"; // add successfully and redirect

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
            return "redirect:/admin/room/add"; // fail and redirect
        }
    }

    /*@PostMapping("/add")
    public String addNewRoom(
            @ModelAttribute Room newRoom,
            @RequestParam("itFacility") List<String> itFacilities,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            // it facility
            newRoom.setItFacility(String.join(",", itFacilities));

            // file upload
            if (!imageFile.isEmpty()) {
                // config file
                //String uploadDir = "uploads/";

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // generate unique file name
                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // save file
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // set file visit path
                newRoom.setImgUrl("/" + uploadDir + uniqueFileName);
            }

            adminRoomService.addNewRoom(newRoom);
            return "redirect:/admin/room";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
            return "redirect:/admin/room/add";
        }
    }*/
    /*@PostMapping("/add")
    public String addNewRoom(@ModelAttribute Room newRoom, @RequestParam("itFacility") List<String> itFacilities) {
        //it facility
        newRoom.setItFacility(String.join(",", itFacilities));
        adminRoomService.addNewRoom(newRoom);
        return "redirect:/admin/room";
    }*/

    // update room information based on room id
    // show the edit page
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable String id, Model model) {
        Room room = adminRoomService.getRoomById(id);
        model.addAttribute("room", room);
        return "UpdateRoom";
    }

    @PostMapping("/update/{id}")
    public String updateRoom(
            @PathVariable String id,
            @ModelAttribute Room updatedRoom,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("removeImageFlag") String removeImageFlag,
            @RequestParam(value = "currentImageUrl", required = false) String currentImageUrl,
            RedirectAttributes redirectAttributes) {

        try {
            // check the update room if exist
            if (adminRoomService.isRoomIdExists(updatedRoom.getId()) && !updatedRoom.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Room ID already exists.");
                return "redirect:/admin/room/update/" + id;
            }

            // delete image logic
            if ("true".equals(removeImageFlag)) {
                currentImageUrl = null;
                updatedRoom.setImgUrl(currentImageUrl);
            }

            // new image logic
            else if (imageFile != null && !imageFile.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                updatedRoom.setImgUrl(accessPath + "/" + uniqueFileName);
            }
            else if (currentImageUrl.isEmpty()) {
                currentImageUrl = null;
                updatedRoom.setImgUrl(currentImageUrl);
            }

            //no image upload and remain the previous one
            else if (updatedRoom.getImgUrl() == null || updatedRoom.getImgUrl().isEmpty()) {
                updatedRoom.setImgUrl(currentImageUrl);
            }

            // update the room information
            adminRoomService.updateRoom(id, updatedRoom, currentImageUrl);
            return "redirect:/admin/room";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/room/update/" + id + "?error=update_failed";
        }
    }

    // based on room id，delete room
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable String id, Model model) {
        try {
            adminRoomService.deleteRoom(id);
            System.out.println("delete ");
            List<Room> allRooms = adminRoomService.getAllRooms();
            model.addAttribute("allRooms", allRooms);
            return "AdminRoomManagement";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to delete room");
            return "AdminRoomManagement";
        }
    }

    // Search room by id
    @GetMapping("/search")
    public String searchRooms(@RequestParam String keyword, Model model) {
        List<Room> allRooms = adminRoomService.searchById(keyword);
        model.addAttribute("allRooms", allRooms);
        model.addAttribute("keyword", keyword);
        System.out.println("Keyword: " + keyword);
        return "AdminRoomManagement";
    }


    //Filter rooms
    @GetMapping("/filter")
    public String filterRooms(
            @RequestParam(name = "itFacility", required = false) String itFacility,
            @RequestParam(name = "availability", required = false) Boolean availability,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "minCapacity", required = false) Integer minCapacity,
            @RequestParam(name = "maxCapacity", required = false) Integer maxCapacity,
            @RequestParam(name = "hasImage", required = false) Boolean hasImage,
            Model model) {

        /*List<Room> filteredRooms = adminRoomService.filterRooms(itFacility, status, location, capacityRange, hasImage);
        model.addAttribute("allRooms", filteredRooms);
        return "room_filter_result"; */

        List<Room> filteredRooms = adminRoomService.filterRooms(itFacility, availability, location, minCapacity, maxCapacity, hasImage);
        model.addAttribute("allRooms", filteredRooms);
        return "AdminRoomManagement";
    }

}
