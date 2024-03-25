package com.demo.user;

import com.demo.uuid.UuidHelper;
import org.springframework.stereotype.Component;

@Component
public class UserApiMapper {

  public UserModel toModel(UserDto dto) {
    return UserModel.builder()
        .id(dto.getId() != null ? UuidHelper.fromString(dto.getId()) : null)
        .name(dto.getName())
        .email(dto.getEmail())
        .code(dto.getCode())
        .build();
  }

  public UserDto toDto(UserModel userModel) {
    return UserDto.builder()
        .id(userModel.getId() != null ? userModel.getId().toString() : null)
        .name(userModel.getName())
        .email(userModel.getEmail())
        .code(userModel.getCode())
        .build();
  }
}