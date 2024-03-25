package com.demo.user;

import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationStatusService {
  
  public UserRegistrationStatusEnum get(UUID userId) {
    return new Random().nextInt(2) + 1 == 1 ?
        UserRegistrationStatusEnum.REGISTERED_ON_CORE :
        UserRegistrationStatusEnum.REGISTERED_ON_PROFILE;
  }
  
  public void save(UUID userId, UserRegistrationStatusEnum registrationStatusEnum) {
    // Save registration status for user id
  }
  
}