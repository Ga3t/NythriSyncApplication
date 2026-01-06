package com.caliq.user_service.services;
import com.caliq.user_service.dto.AboutUserDto;
import com.caliq.user_service.dto.StartSettingDto;
import com.caliq.user_service.models.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
@Service
public interface UserService extends UserDetailsService {
    UserEntity saveUser(UserEntity user);
    UserEntity findById(Long id);
    UserEntity findByUsername(String username);
}