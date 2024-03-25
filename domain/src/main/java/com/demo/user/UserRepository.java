package com.demo.user;

import static java.lang.String.format;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepository {

  private final UserDao dao;

  private final UserMapper mapper;

  public UserModel save(UserModel userModel) {
    if (userModel.getId() == null) {
      return dao.save(mapper.toCreate(userModel, UUID.randomUUID()));
    }

    Optional<UserModel> optionalDbModel = dao.findById(userModel.getId());

    return optionalDbModel
        .map(dbModel -> dao.save(mapper.toUpdate(dbModel, userModel)))
        .orElseThrow(() -> new IllegalArgumentException(format("User with id: [%s] not found", userModel.getId())));
  }

  public Optional<UserModel> getById(UUID id) {
    return dao.findById(id);
  }

}