package com.pewniaczekbet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pewniaczekbet.dto.WinBetPlaceDto;
import com.pewniaczekbet.model.dao.GameRepository;
import com.pewniaczekbet.model.dao.PredictionBetRepository;
import com.pewniaczekbet.model.dao.ScoreBetRepository;
import com.pewniaczekbet.model.dao.SportRepository;
import com.pewniaczekbet.model.dao.TeamRepository;
import com.pewniaczekbet.model.dao.UserPredictionBetRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.dao.UserScoreBetRepository;
import com.pewniaczekbet.model.dao.UserWinBetRepository;
import com.pewniaczekbet.model.dao.WinBetRepository;
import com.pewniaczekbet.model.entities.GameEntity;
import com.pewniaczekbet.model.entities.TeamEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.entities.UserWinBetEntity;
import com.pewniaczekbet.model.entities.WinBetEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.model.exceptions.NotFoundException;
import com.pewniaczekbet.other.ApplicationLimitations;

@ExtendWith(MockitoExtension.class)
class BetServiceTest {

    @Mock
    private WinBetRepository winBetRepository;
    @Mock
    private UserWinBetRepository userWinBetRepository;
    @Mock
    private UserScoreBetRepository userScoreBetRepository;
    @Mock
    private ScoreBetRepository scoreBetRepository;
    @Mock
    private SportRepository sportRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PredictionBetRepository predictionBetRepository;
    @Mock
    private UserPredictionBetRepository userPredictionBetRepository;

    @InjectMocks
    private BetService betService;

    @Captor
    private ArgumentCaptor<UserWinBetEntity> userWinBetCaptor;

    @Test
    void placeWinBet_Success_DeductsFromBalance() {
        Long userId = 1L;
        Long betId = 10L;
        Long amount = 500L;

        TeamEntity team1 = new TeamEntity();
        team1.setId(1L);
        team1.setName("Team A");

        TeamEntity team2 = new TeamEntity();
        team2.setId(2L);
        team2.setName("Team B");

        GameEntity game = new GameEntity();
        game.setId(100L);
        game.setTeam1(team1);
        game.setTeam2(team2);
        game.setName("Game 1");

        WinBetEntity winBet = new WinBetEntity();
        winBet.setId(betId);
        winBet.setName("Win Bet 1");
        winBet.setCurrentMultiplier(1.5);
        winBet.setStopDate(LocalDateTime.now().plusDays(7));
        winBet.setGame(game);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setBalance(1000L);
        user.setFreeBetBalance(2000L);

        WinBetPlaceDto dto = new WinBetPlaceDto();
        dto.setBetId(betId);
        dto.setAmmount(amount);
        dto.setIsFreeBet(false);
        dto.setTeam(true);

        when(winBetRepository.findById(betId)).thenReturn(Optional.of(winBet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        betService.placeWinBet(dto, userId);

        assertEquals(500L, user.getBalance(), "Balance should be reduced by bet amount");
        verify(userRepository).save(user);
        verify(userWinBetRepository).save(userWinBetCaptor.capture());

        UserWinBetEntity saved = userWinBetCaptor.getValue();
        assertEquals(user, saved.getUser());
        assertEquals(amount, saved.getAmmount());
        assertEquals(team2, saved.getTeam(), "team=true should select team2");
        assertEquals(1.5, saved.getMultiplyer());
        assertEquals(winBet, saved.getBet());
    }

    @Test
    void placeWinBet_InsufficientBalance_ThrowsException() {
        Long userId = 1L;
        Long betId = 10L;
        Long amount = 2000L;

        WinBetEntity winBet = new WinBetEntity();
        winBet.setId(betId);
        winBet.setCurrentMultiplier(1.5);
        winBet.setStopDate(LocalDateTime.now().plusDays(7));

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setBalance(1000L);

        WinBetPlaceDto dto = new WinBetPlaceDto();
        dto.setBetId(betId);
        dto.setAmmount(amount);
        dto.setIsFreeBet(false);

        when(winBetRepository.findById(betId)).thenReturn(Optional.of(winBet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> betService.placeWinBet(dto, userId));
        assertTrue(ex.getMessage().contains("insuficient balance"));
        verify(userRepository, never()).save(any());
        verify(userWinBetRepository, never()).save(any());
    }

    @Test
    void placeWinBet_ExpiredBet_ThrowsException() {
        WinBetEntity winBet = new WinBetEntity();
        winBet.setId(1L);
        winBet.setStopDate(LocalDateTime.now().minusDays(1));

        WinBetPlaceDto dto = new WinBetPlaceDto();
        dto.setBetId(1L);
        dto.setAmmount(500L);

        when(winBetRepository.findById(1L)).thenReturn(Optional.of(winBet));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> betService.placeWinBet(dto, 1L));
        assertTrue(ex.getMessage().contains("cloased"));
    }

    @Test
    void placeWinBet_BetNotFound_ThrowsException() {
        when(winBetRepository.findById(99L)).thenReturn(Optional.empty());

        WinBetPlaceDto dto = new WinBetPlaceDto();
        dto.setBetId(99L);
        dto.setAmmount(500L);

        assertThrows(NotFoundException.class, () -> betService.placeWinBet(dto, 1L));
    }

    @Test
    void placeWinBet_BelowMinimumAmount_ThrowsException() {
        WinBetEntity winBet = new WinBetEntity();
        winBet.setId(1L);
        winBet.setStopDate(LocalDateTime.now().plusDays(1));

        WinBetPlaceDto dto = new WinBetPlaceDto();
        dto.setBetId(1L);
        dto.setAmmount((long) ApplicationLimitations.MinBetAmount - 1);

        when(winBetRepository.findById(1L)).thenReturn(Optional.of(winBet));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> betService.placeWinBet(dto, 1L));
        assertTrue(ex.getMessage().contains("To low"));
    }

    @Test
    void placeWinBet_Success_SelectsTeam1WhenTeamFalse() {
        Long userId = 1L;

        TeamEntity team1 = new TeamEntity();
        team1.setId(1L);
        team1.setName("Team A");

        TeamEntity team2 = new TeamEntity();
        team2.setId(2L);
        team2.setName("Team B");

        GameEntity game = new GameEntity();
        game.setId(100L);
        game.setTeam1(team1);
        game.setTeam2(team2);

        WinBetEntity winBet = new WinBetEntity();
        winBet.setId(1L);
        winBet.setCurrentMultiplier(2.0);
        winBet.setStopDate(LocalDateTime.now().plusDays(7));
        winBet.setGame(game);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setBalance(1000L);

        WinBetPlaceDto dto = new WinBetPlaceDto();
        dto.setBetId(1L);
        dto.setAmmount(200L);
        dto.setIsFreeBet(false);
        dto.setTeam(false);

        when(winBetRepository.findById(1L)).thenReturn(Optional.of(winBet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        betService.placeWinBet(dto, userId);

        verify(userWinBetRepository).save(userWinBetCaptor.capture());
        assertEquals(team1, userWinBetCaptor.getValue().getTeam(),
                "team=false should select team1");
    }

    @Test
    void placeWinBet_Success_DeductsFromFreeBetBalance() {
        Long userId = 1L;

        WinBetEntity winBet = new WinBetEntity();
        winBet.setId(1L);
        winBet.setCurrentMultiplier(1.5);
        winBet.setStopDate(LocalDateTime.now().plusDays(7));

        TeamEntity team1 = new TeamEntity();
        team1.setId(1L);
        team1.setName("Team A");
        TeamEntity team2 = new TeamEntity();
        team2.setId(2L);
        team2.setName("Team B");
        GameEntity game = new GameEntity();
        game.setTeam1(team1);
        game.setTeam2(team2);
        winBet.setGame(game);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setBalance(1000L);
        user.setFreeBetBalance(2000L);

        WinBetPlaceDto dto = new WinBetPlaceDto();
        dto.setBetId(1L);
        dto.setAmmount(500L);
        dto.setIsFreeBet(true);
        dto.setTeam(false);

        when(winBetRepository.findById(1L)).thenReturn(Optional.of(winBet));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        betService.placeWinBet(dto, userId);

        assertEquals(1500L, user.getFreeBetBalance(), "Free bet balance should be reduced");
        assertEquals(1000L, user.getBalance(), "Regular balance should not change");
    }
}
