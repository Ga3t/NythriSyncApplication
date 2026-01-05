package com.caliq.user_service.controllers;


import com.caliq.user_service.dto.AboutUserDto;
import com.caliq.user_service.dto.StartSettingDto;
import com.caliq.user_service.repository.UserRepository;
import com.caliq.user_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {


    private UserService userService;
    private UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

//    @GetMapping("/about")
//    public ResponseEntity<AboutUserDto> getAboutUser (String userId){
//
//        AboutUserDto response = userService.aboutUser(userId);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @PostMapping("/setup")
//    public ResponseEntity<StartSettingDto> setUpAboutUser(
//            @RequestBody StartSettingDto startSettingDto,
//            @RequestHeader ("X-User-ID") String userId){
//        StartSettingDto response= new StartSettingDto();
//        response = userService.setUpUser(startSettingDto, userId);
//        return null;
//    }
}
