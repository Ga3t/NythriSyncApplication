package com.caliq.user_service.services.impl;

import com.caliq.core.enums.SexEnums;
import com.caliq.user_service.dto.AboutUserDto;
import com.caliq.user_service.dto.StartSettingDto;
import com.caliq.user_service.exceptions.UserNotFoundException;
import com.caliq.user_service.models.UserEntity;
import com.caliq.user_service.repository.UserRepository;
import com.caliq.user_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;


@Service
@Primary
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Username not found"));
        return new User(user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public UserEntity findById(Long id) {
        return null;
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

//    @Override
//    @Transactional
//    public AboutUserDto aboutUser(String userId) {
//
//        Long userID = Long.parseLong(userId);
//
//        UserEntity userEntity = userRepository.findById(userID)
//                .orElseThrow(()-> new UserNotFoundException("No such User"));
//        AboutUserDto aboutUserDto = new AboutUserDto();
//        aboutUserDto.setUsername(userEntity.getUsername());
//        aboutUserDto.setEmail(userEntity.getEmail());
//        int age = Period.between(userEntity.getBirthdayDate(), LocalDate.now()).getYears();
//        aboutUserDto.setAge(age);
//        return aboutUserDto;
//    }
//
//    @Override
//    @Transactional
//    public StartSettingDto setUpUser(StartSettingDto startSettingDto, String userId) {
//
//        Long userID = Long.parseLong(userId);
//        UserEntity userEntity = userRepository.findById(userID)
//                .orElseThrow(()-> new UserNotFoundException("No such User"));
//        BigDecimal weight = startSettingDto.getWeight();
//        BigDecimal growth = startSettingDto.getGrowth();
//        BigDecimal age;
//
//        if(startSettingDto.getBirthdayDate() !=null){
//            userEntity.setBirthdayDate(startSettingDto.getBirthdayDate());
//            age= BigDecimal.valueOf(Period.between(startSettingDto.getBirthdayDate(), LocalDate.now()).getYears());
//        }if(startSettingDto.getAge() != null){
//            age = BigDecimal.valueOf(startSettingDto.getAge());
//        }else{
//            throw new IllegalArgumentException();
//        }
//        BigDecimal bmr;
//
//        if (startSettingDto.getSex() == SexEnums.MALE) {
//            bmr = BigDecimal.TEN.multiply(weight)
//                    .add(BigDecimal.valueOf(6.25).multiply(growth))
//                    .subtract(BigDecimal.valueOf(5).multiply(age))
//                    .add(BigDecimal.valueOf(5));
//            System.out.println(bmr);
//        } else if (startSettingDto.getSex() == SexEnums.FEMALE) {
//            bmr = BigDecimal.TEN.multiply(weight)
//                    .add(BigDecimal.valueOf(6.25).multiply(growth))
//                    .subtract(BigDecimal.valueOf(5).multiply(age))
//                    .subtract(BigDecimal.valueOf(161));
//            System.out.println(bmr);
//        } else {
//            throw new IllegalArgumentException("Invalid sex value");
//        }
//        //TODO отправлять дальше в сервис с подсчетом уже готовый подсчет и возможно вес?
//
//        return null;
//    }
}
