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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pewniaczekbet.dto.OathDto;
import com.pewniaczekbet.model.dao.OathRepository;
import com.pewniaczekbet.model.dao.OathServiceRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.entities.OathEntity;
import com.pewniaczekbet.model.entities.OathServiceEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.model.exceptions.InternalServerErrorException;

@ExtendWith(MockitoExtension.class)
class OathServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OathRepository oathRepository;
    @Mock
    private OathServiceRepository oathServiceRepository;

    private OathServie oathService;

    @BeforeEach
    void setUp() throws Exception {
        oathService = new OathServie(userRepository, oathRepository, oathServiceRepository);

        setField("clientId", "test_client_id");
        setField("clientSecret", "test_client_secret");
        setField("redirectUri", "https://example.com/callback/");
    }

    private void setField(String name, String value) throws Exception {
        Field field = OathServie.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(oathService, value);
    }

    @Test
    void getOath_ReturnsList() {
        OathServiceEntity githubService = new OathServiceEntity();
        githubService.setName("github");

        OathEntity entity = new OathEntity();
        entity.setLogin("jan123");
        entity.setAvatarUrl("https://avatars.example.com/1");
        entity.setUrl("https://github.com/jan123");
        entity.setService(githubService);

        when(oathRepository.findByUserId(1L)).thenReturn(List.of(entity));

        List<OathDto> result = oathService.getOath(1L);

        assertEquals(1, result.size());
        assertEquals("jan123", result.get(0).getLogin());
        assertEquals("github", result.get(0).getService());
        assertEquals("https://avatars.example.com/1", result.get(0).getAvatarUrl());
    }

    @Test
    void getOath_ReturnsEmptyList() {
        when(oathRepository.findByUserId(1L)).thenReturn(List.of());

        List<OathDto> result = oathService.getOath(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getRedirectGithub_Success() {
        when(oathRepository.findOneByUserIdAndServiceName(1L, "github")).thenReturn(Optional.empty());

        String url = oathService.getRedirectGithub(1L);

        assertTrue(url.startsWith("https://github.com/login/oauth/authorize?"));
        assertTrue(url.contains("client_id=test_client_id"));
        assertTrue(url.contains("redirect_uri=https://example.com/callback/github/callback"));
        assertTrue(url.contains("scope=user:email,read:user"));
    }

    @Test
    void getRedirectGithub_AlreadyLinked_ThrowsException() {
        when(oathRepository.findOneByUserIdAndServiceName(1L, "github"))
                .thenReturn(Optional.of(new OathEntity()));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> oathService.getRedirectGithub(1L));
        assertTrue(ex.getMessage().contains("alredy"));
    }

    @Test
    void deleteGithub_Success() {
        OathEntity entity = new OathEntity();
        entity.setId(5L);

        when(oathRepository.findOneByUserIdAndServiceName(1L, "github"))
                .thenReturn(Optional.of(entity));

        oathService.deleteGithub(1L);

        verify(oathRepository).deleteById(5L);
    }

    @Test
    void deleteGithub_NotLinked_ThrowsException() {
        when(oathRepository.findOneByUserIdAndServiceName(1L, "github"))
                .thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> oathService.deleteGithub(1L));
        assertTrue(ex.getMessage().contains("not linked"));
        verify(oathRepository, never()).deleteById(any());
    }

    @Test
    void getCallbackGithub_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        InternalServerErrorException ex = assertThrows(InternalServerErrorException.class,
                () -> oathService.getCallbackGithub("code123", 1L));
        assertTrue(ex.getMessage().contains("unable to find user"));
    }
}
