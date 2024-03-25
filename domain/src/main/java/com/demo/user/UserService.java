package com.demo.user;

import com.demo.core.CoreWebClient;
import com.demo.license.LicenseWebClient;
import com.demo.profile.ProfileWebClient;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository repository;
  private final ProfileWebClient profileWebClient;
  private final LicenseWebClient licenseWebClient;
  private final CoreWebClient coreWebClient;
  private final UserRegistrationStatusService registrationStatusService;

  public Optional<UserModel> getById(UUID id) {
    return repository.getById(id);
  }

  public UserModel save(UserModel model) {
    return repository.save(model);
  }

  public void register(UUID id) {
    Optional<UserModel> model = repository.getById(id);
    
    if (model.isEmpty()) {
      throw new RuntimeException("Cannot register a non existent user", new NoSuchElementException());
    }

    UserRegistrationStatusEnum registrationStatusEnum = registrationStatusService.get(id);

    if (licenseWebClient.userHasAccess(id)) {
      if (registrationStatusEnum == null) {
        if (!profileWebClient.userIsRegistered(id)) {
          String registeredEmail = profileWebClient.registerUser(id, model.get().getEmail());

          if (!model.get().getEmail().equalsIgnoreCase(registeredEmail)) {
            coreWebClient.registerUser(id, model.get().getEmail(), registeredEmail);
            registrationStatusService.save(id, UserRegistrationStatusEnum.REGISTERED_ON_CORE);
          } else {
            registrationStatusService.save(id, UserRegistrationStatusEnum.REGISTERED_ON_PROFILE);
          }
        }
      } else if (UserRegistrationStatusEnum.REGISTERED_ON_PROFILE.equals(registrationStatusEnum)) {
        String registeredEmail = profileWebClient.getRegisteredEmail(id);

        if (!model.get().getEmail().equalsIgnoreCase(registeredEmail)) {
          coreWebClient.registerUser(id, model.get().getEmail(), registeredEmail);
          registrationStatusService.save(id, UserRegistrationStatusEnum.REGISTERED_ON_CORE);
        }
      }
    } else {
      registrationStatusService.save(id, UserRegistrationStatusEnum.NO_ACCESS_TO_BE_REGISTERED);

      throw new RuntimeException("User has no access to be registered", new IllegalArgumentException());
    }
  }

}