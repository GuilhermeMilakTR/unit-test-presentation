package com.demo.user; 

import com.demo.user.UserModel;
import com.demo.user.UserRepository;
import com.demo.user.UserDao;
import com.demo.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryTests {

  @Mock
  private UserDao userDao;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserRepository userRepository;

  private UUID id;
  private UserModel userModel;
  private UserModel dbModel;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    
    id = UUID.randomUUID();
    userModel = new UserModel();
    dbModel = new UserModel();
  }

  @Test
  public void saveNewUser() {
    when(userModel.getId()).thenReturn(null);
    when(userMapper.toCreate(userModel, id)).thenReturn(dbModel);
    when(userDao.save(dbModel)).thenReturn(dbModel);

    UserModel result = userRepository.save(userModel);

    verify(userDao, times(1)).save(dbModel);
    assertEquals(dbModel, result);
  }

  @Test
  public void saveExistingUser() {
    when(userModel.getId()).thenReturn(id);
    when(userDao.findById(id)).thenReturn(Optional.of(dbModel));
    when(userMapper.toUpdate(dbModel, userModel)).thenReturn(dbModel);
    when(userDao.save(dbModel)).thenReturn(dbModel);

    UserModel result = userRepository.save(userModel);

    verify(userDao, times(1)).save(dbModel);
    assertEquals(dbModel, result);
  }

  @Test
  public void saveNonExistingUser() {
    when(userModel.getId()).thenReturn(id);
    when(userDao.findById(id)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> userRepository.save(userModel));
  }

  @Test
  public void getByIdExistingUser() {
    when(userDao.findById(id)).thenReturn(Optional.of(dbModel));

    Optional<UserModel> result = userRepository.getById(id);

    verify(userDao, times(1)).findById(id);
    assertEquals(Optional.of(dbModel), result);
  }

  @Test
  public void getByIdNonExistingUser() {
    when(userDao.findById(id)).thenReturn(Optional.empty());

    Optional<UserModel> result = userRepository.getById(id);

    verify(userDao, times(1)).findById(id);
    assertEquals(Optional.empty(), result);
  }
}