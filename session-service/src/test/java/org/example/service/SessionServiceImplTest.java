package org.example.service;

import org.example.exception.CourierAlreadyHasActiveSessionException;
import org.example.exception.SessionNotFoundException;
import org.example.model.Session;
import org.example.repository.SessionRepository;
import org.example.rest.dto.SessionDto;
import org.example.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionServiceImpl sessionServiceImpl;

    private Session session;
    private final Long TEST_ID = 1L;
    private final String TEST_COURIER = "test-courier";

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setId(TEST_ID);
        session.setCourierId(TEST_COURIER);
        session.setStatus("ACTIVE");
        session.setStartTime(LocalDateTime.now());
    }

    @Test
    void start_ShouldCreateNewSession_WhenCourierNotActive() {
        // given
        when(sessionRepository.findActiveByCourierId(TEST_COURIER))
                .thenReturn(Optional.empty());
        when(sessionRepository.save(any(Session.class)))
                .thenReturn(session);

        // when
        SessionDto result = sessionServiceImpl.start(TEST_COURIER);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getCourierId()).isEqualTo(TEST_COURIER);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");

        verify(sessionRepository).findActiveByCourierId(TEST_COURIER);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void start_ShouldThrowException_WhenCourierAlreadyActive() {
        // given
        when(sessionRepository.findActiveByCourierId(TEST_COURIER))
                .thenReturn(Optional.of(session));

        // when/then
        assertThatThrownBy(() -> sessionServiceImpl.start(TEST_COURIER))
                .isInstanceOf(CourierAlreadyHasActiveSessionException.class)
                .hasMessageContaining(TEST_COURIER);

        verify(sessionRepository).findActiveByCourierId(TEST_COURIER);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getSessionById_ShouldReturnSession_WhenExists() {
        // given
        when(sessionRepository.findById(TEST_ID))
                .thenReturn(Optional.of(session));

        // when
        SessionDto result = sessionServiceImpl.getSessionById(TEST_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getCourierId()).isEqualTo(TEST_COURIER);

        verify(sessionRepository).findById(TEST_ID);
    }

    @Test
    void getSessionById_ShouldThrowException_WhenNotFound() {
        // given
        when(sessionRepository.findById(TEST_ID))
                .thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> sessionServiceImpl.getSessionById(TEST_ID))
                .isInstanceOf(SessionNotFoundException.class)
                .hasMessageContaining(String.valueOf(TEST_ID));
    }
}