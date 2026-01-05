package com.caliq.calorie_service.controller;


import com.caliq.calorie_service.models.dto.UpdateUserDetailsDto;
import com.caliq.calorie_service.models.dto.UserDetailsDto;
import com.caliq.calorie_service.models.dto.UserInfoResponse;
import com.caliq.calorie_service.models.response.UserDetailsResponse;
import com.caliq.calorie_service.models.response.WeightLogsResponse;
import com.caliq.calorie_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/userdetails")
public class UserController {


    private UserService userService;

    public UserController(UserService userDetailsService) {
        this.userService = userDetailsService;
    }

    @PostMapping("/setuserdetails")
    public ResponseEntity<UserDetailsResponse> setUserDetails(@RequestBody UserDetailsDto userDetails,
                                                              @RequestHeader("X-User-ID")String userId){
        Long id = Long.valueOf(userId);
        UserDetailsResponse userDetailsResponse = userService.saveUserDetails(userDetails, id);
        return ResponseEntity.ok(userDetailsResponse);
    }

    @GetMapping("/userdetailsexists")
    public ResponseEntity<Boolean> userDetailsExists(@RequestHeader("X-User-ID")String userId){
        Long id = Long.valueOf(userId);
        return ResponseEntity.ok(userService.detailsExists(id));
    }

    @PostMapping("/updateuserdetails")
    public ResponseEntity<String>  updateUserDetails(@RequestBody UpdateUserDetailsDto updateUserDetailsDto,
                                                     @RequestHeader("X-User-ID")String userId){
        Long id = Long.valueOf(userId);
        return ResponseEntity.ok(userService.updateUserDetails(updateUserDetailsDto, id));
    }

    @GetMapping("/newWeighing")
    public ResponseEntity<BigDecimal> setNewWeighing(@RequestParam("new_weight")BigDecimal newWeight,
                                                     @RequestHeader("X-User-ID")String userId){
        Long id = Long.valueOf(userId);
        return ResponseEntity.ok(userService.setNewWeight(newWeight, id));
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> userInfo(@RequestHeader("X-User-ID") String userId){

        Long id  = Long.valueOf(userId);
        return ResponseEntity.ok(userService.getUserInfo(id));
    }
}
