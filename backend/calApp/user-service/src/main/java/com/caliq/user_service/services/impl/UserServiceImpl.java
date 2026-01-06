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
}