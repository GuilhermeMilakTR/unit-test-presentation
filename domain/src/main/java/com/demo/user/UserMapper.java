package com.demo.user;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserModel toCreate(UserModel userModel, UUID uuid) {
    return userModel.toBuilder()
        .id(uuid)
        .build();
  }

  public UserModel toUpdate(UserModel model, UserModel updatedModel) {
    return model.toBuilder()
        .name(updatedModel.getName())
        .email(updatedModel.getEmail())
        .build();
  }
  
}