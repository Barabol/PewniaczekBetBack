package com.pewniaczekbet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.pewniaczekbet.dto.LoginUserDto;
import com.pewniaczekbet.dto.NewUserDto;
import com.pewniaczekbet.dto.UserDto;
import com.pewniaczekbet.model.dao.FollowerRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.entities.FollowEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.other.ApplicationLimitations;
import com.pewniaczekbet.other.FollowId;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowerRepository followRepository;
    @Mock
    private EntityManager entityManager;

    private UserService userService;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @BeforeEach
    void setUp() throws Exception {
        userService = new UserService(userRepository, followRepository);
        Field field = UserService.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(userService, entityManager);
    }

    @Test
    void createUser_Success() {
        NewUserDto dto = new NewUserDto();
        dto.setName("Jan");
        dto.setSurname("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPassword("secret123");

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setName("Jan");
        savedEntity.setSurname("Kowalski");
        savedEntity.setEmail("jan@example.com");
        savedEntity.setPassword("$2a$10$hashedpassword");
        savedEntity.setBalance(0L);
        savedEntity.setFreeBetBalance(ApplicationLimitations.NewAccountFreeBet);
        savedEntity.setWins(0L);
        savedEntity.setLosses(0L);
        savedEntity.setWinsAmount(0L);
        savedEntity.setLossesAmount(0L);
        savedEntity.setPublic(true);
        savedEntity.setAccountTypeId(0L);

        when(userRepository.save(any())).thenReturn(savedEntity);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("Jan", result.getName());
        assertEquals("Kowalski", result.getSurname());
        assertEquals(0L, result.getBalance());
        assertEquals(ApplicationLimitations.NewAccountFreeBet, result.getFreeBetBalance());
        assertEquals(0L, result.getWins());
        assertEquals(0L, result.getLosses());
        assertTrue(result.isPublic());

        verify(userRepository).save(userCaptor.capture());
        UserEntity captured = userCaptor.getValue();
        assertNotEquals("secret123", captured.getPassword(), "Password should be hashed");
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        NewUserDto dto = new NewUserDto();
        dto.setEmail("jan@example.com");
        dto.setPassword("secret123");

        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(BadRequestException.class, () -> userService.createUser(dto));
    }

    @Test
    void login_Success() {
        String email = "jan@example.com";
        String rawPassword = "secret123";
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt());

        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setEmail(email);
        entity.setPassword(hashed);
        entity.setName("Jan");
        entity.setPublic(true);

        when(userRepository.findByEmail(email)).thenReturn(entity);

        LoginUserDto dto = new LoginUserDto();
        dto.setEmail(email);
        dto.setPassword(rawPassword);

        UserDto result = userService.login(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getName());
    }

    @Test
    void login_WrongEmail_ThrowsException() {
        when(userRepository.findByEmail("wrong@example.com")).thenReturn(null);

        LoginUserDto dto = new LoginUserDto();
        dto.setEmail("wrong@example.com");
        dto.setPassword("any");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> userService.login(dto));
        assertTrue(ex.getMessage().contains("bad email or password"));
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        String email = "jan@example.com";
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("correct", org.mindrot.jbcrypt.BCrypt.gensalt());

        UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPassword(hashed);

        when(userRepository.findByEmail(email)).thenReturn(entity);

        LoginUserDto dto = new LoginUserDto();
        dto.setEmail(email);
        dto.setPassword("wrong");

        assertThrows(BadRequestException.class, () -> userService.login(dto));
    }

    @Test
    void getUsers_ReturnsAll() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setName("Jan");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setName("Anna");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("Jan", result.get(0).getName());
        assertEquals("Anna", result.get(1).getName());
    }

    @Test
    void follow_Success() {
        Long followerId = 1L;
        Long followedId = 2L;

        UserEntity follower = new UserEntity();
        follower.setId(followerId);

        UserEntity followed = new UserEntity();
        followed.setId(followedId);

        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.empty());
        when(userRepository.findById(followedId)).thenReturn(Optional.of(followed));
        when(entityManager.find(any(), eq(followerId))).thenReturn(follower);

        userService.follow(followerId, followedId);

        verify(followRepository).save(any(FollowEntity.class));
    }

    @Test
    void follow_AlreadyFollowing_ThrowsException() {
        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.of(new FollowEntity()));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.follow(1L, 2L));
        assertTrue(ex.getMessage().contains("already following"));
        verify(followRepository, never()).save(any());
    }

    @Test
    void follow_SelfFollow_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.follow(1L, 1L));
        verify(followRepository, never()).save(any());
    }

    @Test
    void follow_NullIds_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.follow(null, 1L));
        assertThrows(BadRequestException.class, () -> userService.follow(1L, null));
    }

    @Test
    void unfollow_Success() {
        Long followerId = 1L;
        Long followedId = 2L;

        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.of(new FollowEntity()));

        userService.unfollow(followerId, followedId);

        verify(followRepository).deleteById(any(FollowId.class));
    }

    @Test
    void unfollow_NotFollowing_ThrowsException() {
        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.unfollow(1L, 2L));
        assertTrue(ex.getMessage().contains("unable to unfollow"));
        verify(followRepository, never()).deleteById(any());
    }

    @Test
    void unfollow_SelfUnfollow_ThrowsException() {
        assertThrows(BadRequestException.class, () -> userService.unfollow(1L, 1L));
    }

    @Test
    void toggleVisibility_PublicToPrivate() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setPublic(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.toggleVisibility(1L);

        assertFalse(result.isPublic());
        verify(userRepository).save(argThat(u -> !u.isPublic()));
    }

    @Test
    void toggleVisibility_PrivateToPublic() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setPublic(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.toggleVisibility(1L);

        assertTrue(result.isPublic());
    }

    @Test
    void toggleVisibility_UserNotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userService.toggleVisibility(99L));
    }

    @Test
    void getDetails_OwnProfile_ReturnsFullData() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setBalance(5000L);
        entity.setFreeBetBalance(1000L);
        entity.setWins(10L);
        entity.setLosses(2L);
        entity.setWinsAmount(50000L);
        entity.setLossesAmount(10000L);
        entity.setPublic(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.getDetails(1L, 1L);

        assertEquals(5000L, result.getBalance());
        assertEquals(1000L, result.getFreeBetBalance());
        assertEquals(10L, result.getWins());
        assertEquals(2L, result.getLosses());
    }

    @Test
    void getDetails_PublicProfile_ReturnsFullData() {
        UserEntity entity = new UserEntity();
        entity.setId(2L);
        entity.setBalance(5000L);
        entity.setWins(10L);
        entity.setPublic(true);

        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));

        UserDto result = userService.getDetails(1L, 2L);

        assertEquals(5000L, result.getBalance());
        assertEquals(10L, result.getWins());
    }

    @Test
    void getDetails_PrivateProfileNoFollow_HidesStats() {
        UserEntity entity = new UserEntity();
        entity.setId(2L);
        entity.setBalance(9999L);
        entity.setFreeBetBalance(500L);
        entity.setWins(10L);
        entity.setLosses(2L);
        entity.setWinsAmount(50000L);
        entity.setLossesAmount(10000L);
        entity.setPublic(false);

        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.empty());

        UserDto result = userService.getDetails(1L, 2L);

        assertEquals(0L, result.getBalance());
        assertEquals(0L, result.getFreeBetBalance());
        assertEquals(0L, result.getWins());
        assertEquals(0L, result.getLosses());
        assertEquals(0L, result.getWinsAmount());
        assertEquals(0L, result.getLossesAmount());
    }

    @Test
    void getDetails_PrivateProfileWithFollow_ReturnsFullData() {
        UserEntity entity = new UserEntity();
        entity.setId(2L);
        entity.setBalance(9999L);
        entity.setFreeBetBalance(500L);
        entity.setWins(10L);
        entity.setLosses(2L);
        entity.setWinsAmount(50000L);
        entity.setLossesAmount(10000L);
        entity.setPublic(false);

        when(userRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.of(new FollowEntity()));

        UserDto result = userService.getDetails(1L, 2L);

        assertEquals(9999L, result.getBalance());
        assertEquals(500L, result.getFreeBetBalance());
        assertEquals(10L, result.getWins());
        assertEquals(2L, result.getLosses());
        assertEquals(50000L, result.getWinsAmount());
        assertEquals(10000L, result.getLossesAmount());
    }

    @Test
    void getDetails_UserNotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> userService.getDetails(1L, 99L));
    }

    @Test
    void getDetails_NullUserId_DefaultsToRequestId() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setBalance(3000L);
        entity.setPublic(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        UserDto result = userService.getDetails(1L, null);

        assertNotNull(result);
        assertEquals(3000L, result.getBalance());
    }
}
