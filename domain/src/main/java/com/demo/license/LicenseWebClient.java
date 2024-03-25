package com.demo.license;

import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LicenseWebClient {
  
  public boolean userHasAccess(UUID id) {
    return new Random().nextInt(2) + 1 == 1;
  }
  
}