package com.demo.user;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.demo.core.CoreWebClient;
import com.demo.license.LicenseWebClient;
import com.demo.profile.ProfileWebClient;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

  private static final UUID USER_ID = UUID.randomUUID();
  
  @Mock
  private UserRepository repository;

  @Mock
  private ProfileWebClient profileWebClient;

  @Mock
  private LicenseWebClient licenseWebClient;

  @Mock
  private CoreWebClient coreWebClient;

  @Mock
  private UserRegistrationStatusService registrationStatusService;

  @InjectMocks
  private UserService service;

  @BeforeEach
  public void setup() {
    
  }
  
  @Test
  void shouldNotRegisterUserWhenItDoesNotExist() {
    // Arrange
    when(repository.getById(any())).thenReturn(Optional.empty());

    // Act
    Exception exception = assertThrows(RuntimeException.class, () -> service.register(USER_ID));
    
    // Assert
    assertInstanceOf(NoSuchElementException.class, exception.getCause());
    verify(repository).getById(USER_ID);
    verifyNoInteractions(profileWebClient, licenseWebClient, coreWebClient, registrationStatusService);
  }

  @Test
  void givenThatUserDoesNotHaveAccess_whenCallToRegister_thenShouldSkipUserRegistrationAndSaveNoAccessStatus() {
    // Arrange
    UUID userId = UUID.randomUUID();

    UserModel model = UserModel.builder()
        .id(userId)
        .email("email@email.com")
        .build();

    when(repository.getById(any())).thenReturn(Optional.of(model));
    when(licenseWebClient.userHasAccess(any())).thenReturn(false);

    // Act
    Exception exception = assertThrows(RuntimeException.class, () -> service.register(userId));
    
    // Assert
    assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    
    // Não verificou se o user foi encontrado (sem necessidade, já é implícito que foi encontrado)
    verify(licenseWebClient).userHasAccess(userId);
    verify(registrationStatusService).save(userId, UserRegistrationStatusEnum.NO_ACCESS_TO_BE_REGISTERED);
    verifyNoInteractions(profileWebClient, coreWebClient);
  }

  @Test
  void shouldNotRegisterUserWhenItIsAlreadyRegistered() {
    UUID userId = UUID.randomUUID();

    UserModel model = UserModel.builder()
        .id(userId)
        .email("email@email.com")
        .build();

    when(repository.getById(any())).thenReturn(Optional.of(model));
    when(licenseWebClient.userHasAccess(any())).thenReturn(true);
    when(profileWebClient.userIsRegistered(any())).thenReturn(true);

    // Act
    service.register(userId);

    // Assert
    verify(profileWebClient).userIsRegistered(userId);
    verify(profileWebClient, never()).registerUser(any(), any());
    verifyNoMoreInteractions(profileWebClient); // Não é pra chamar o profileWebClient.registerUser()
    verifyNoInteractions(coreWebClient);
  }

  @Test
  void givenThatUserHasTheSameRegisteredEmail_whenCallToRegister_thenShouldNotCallCoreWebClientAndSaveStatusAsRegisteredOnProfile() {
    UUID userId = UUID.randomUUID();
    String userEmail = "email@email.com";

    UserModel model = UserModel.builder()
        .id(userId)
        .email(userEmail)
        .build();

    when(repository.getById(any())).thenReturn(Optional.of(model));
    when(licenseWebClient.userHasAccess(any())).thenReturn(true);
    when(profileWebClient.userIsRegistered(any())).thenReturn(false);
    when(profileWebClient.registerUser(any(), any())).thenReturn(userEmail);

    service.register(userId);

    verify(profileWebClient).registerUser(userId, userEmail);
    verify(registrationStatusService).save(userId, UserRegistrationStatusEnum.REGISTERED_ON_PROFILE);
    verifyNoInteractions(coreWebClient);
  }

  @Test
  void shouldRegisterUserAndCallCoreAndSaveStatusAsRegisteredOnCoreWhenItIsNotRegisteredAndHaveDifferentEmails() {
    UUID userId = UUID.randomUUID();
    String userEmail = "email@email.com";
    String registeredEmail = "registered-email@email.com";

    UserModel model = UserModel.builder()
        .id(userId)
        .email(userEmail)
        .build();

    when(repository.getById(any())).thenReturn(Optional.of(model));
    when(licenseWebClient.userHasAccess(any())).thenReturn(true);
    when(profileWebClient.userIsRegistered(any())).thenReturn(false);
    when(profileWebClient.registerUser(any(), any())).thenReturn(registeredEmail);

    // Usando "doNothing()" por ser um método void
    doNothing().when(coreWebClient).registerUser(any(), any(), any());

    service.register(userId);

    // Verificar todas as chamadas
    verify(coreWebClient).registerUser(userId, userEmail, registeredEmail);
    verify(registrationStatusService).save(userId, UserRegistrationStatusEnum.REGISTERED_ON_CORE);
  }

}