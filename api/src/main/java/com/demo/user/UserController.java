package com.demo.user;

import static java.lang.String.format;

import com.demo.uuid.UuidHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserApiMapper mapper;
  private final UserService service;

  @PostMapping
  public UserDto create(@RequestBody UserDto dto) {
    UserModel savedModel = service.save(mapper.toModel(dto));

    return mapper.toDto(savedModel);
  }

  @PutMapping("/{id}")
  public UserDto update(@PathVariable String id, @RequestBody UserDto dto) {
    if (Strings.isBlank(id) || UuidHelper.fromString(id) == null) {
      throw new IllegalArgumentException("Id is invalid");
    }

    UserModel savedModel = service.save(mapper.toModel(dto));

    return mapper.toDto(savedModel);
  }

  @GetMapping("/{id}")
  public UserDto getById(@PathVariable String id) {
    if (Strings.isBlank(id) || UuidHelper.fromString(id) == null) {
      throw new IllegalArgumentException("Id is invalid");
    }

    return service.getById(UuidHelper.fromString(id))
        .map(mapper::toDto)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("User with id: [%s] not found", id)));
  }

  @PostMapping("/{id}/register")
  public void register(@PathVariable String id) {
    if (Strings.isBlank(id) || UuidHelper.fromString(id) == null) {
      throw new IllegalArgumentException("Id is invalid");
    }

    service.register(UuidHelper.fromString(id));
  }

}