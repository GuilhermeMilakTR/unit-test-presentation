package com.demo.profile;

import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProfileWebClient {


  public boolean userIsRegistered(UUID id) {
    return new Random().nextInt(2) + 1 == 1;
  }

  public String getRegisteredEmail(UUID id) {
    return "email.registered@email.com";
  }
  
  public String registerUser(UUID id, String email) {
    int random = new Random().nextInt(2) + 1;

    if (random == 1) {
      return email + ".registered";
    } else {
      return email;
    }
  }
  
}