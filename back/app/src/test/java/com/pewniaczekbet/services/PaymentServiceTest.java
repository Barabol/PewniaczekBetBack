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

import com.pewniaczekbet.model.dao.PaymentRepository;
import com.pewniaczekbet.model.dao.PaymentStatusRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.entities.PaymentEntity;
import com.pewniaczekbet.model.entities.PaymentStatusEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.other.ApplicationLimitations;
import com.stripe.model.checkout.Session;
import com.stripe.exception.StripeException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentStatusRepository paymentStatusRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private StripeService stripeService;

    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<PaymentEntity> paymentCaptor;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @BeforeEach
    void setUp() throws Exception {
        paymentService = new PaymentService(userRepository, paymentStatusRepository, paymentRepository, stripeService);

        setField("stripePublicKey", "pk_test_xxx");
        setField("stripeRedirectSuccess", "https://example.com/success");
        setField("stripeRedirectCancel", "https://example.com/cancel");
    }

    private void setField(String name, String value) throws Exception {
        Field field = PaymentService.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(paymentService, value);
    }

    /*--createPayment amount validation--*/

    @Test
    void createPayment_AmountTooLow_ThrowsException() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> paymentService.createPayment((long) ApplicationLimitations.MinPayment - 1, 1L));
        assertTrue(ex.getMessage().contains("Bad payment amount"));
    }

    @Test
    void createPayment_AmountTooHigh_ThrowsException() {
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> paymentService.createPayment((long) ApplicationLimitations.MaxPayment + 1, 1L));
        assertTrue(ex.getMessage().contains("Bad payment amount"));
    }

    @Test
    void createPayment_UserNotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> paymentService.createPayment(500L, 99L));
    }

    /*--handleSuccess--*/

    @Test
    void handleSuccess_PaymentNotFound_ThrowsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> paymentService.handleSuccess(99L));
    }

    @Test
    void handleSuccess_StatusUnchanged_ThrowsException() {
        PaymentStatusEntity status = new PaymentStatusEntity();
        status.setName("unpaid");

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setSid("cs_test_123");
        payment.setStatus(status);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        try (var sessionMock = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            sessionMock.when(() -> Session.retrieve("cs_test_123")).thenReturn(session);
            when(session.getPaymentStatus()).thenReturn("unpaid");

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> paymentService.handleSuccess(1L));
            assertTrue(ex.getMessage().contains("Dead transaction"));
        }
    }

    @Test
    void handleSuccess_StatusChangedToPaid_IncreasesBalance() throws StripeException {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setBalance(5000L);

        PaymentStatusEntity unpaidStatus = new PaymentStatusEntity();
        unpaidStatus.setId(1L);
        unpaidStatus.setName("unpaid");

        PaymentStatusEntity paidStatus = new PaymentStatusEntity();
        paidStatus.setId(2L);
        paidStatus.setName("paid");

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setSid("cs_test_123");
        payment.setAmount(2000L);
        payment.setUser(user);
        payment.setStatus(unpaidStatus);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentStatusRepository.findByName("paid")).thenReturn(Optional.of(paidStatus));

        try (var sessionMock = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            sessionMock.when(() -> Session.retrieve("cs_test_123")).thenReturn(session);
            when(session.getPaymentStatus()).thenReturn("paid");

            paymentService.handleSuccess(1L);

            assertEquals(7000L, user.getBalance(), "Balance should be increased by payment amount");
            assertEquals("paid", payment.getStatus().getName());
            verify(userRepository).save(user);
            verify(paymentRepository).save(payment);
        }
    }

    @Test
    void handleSuccess_StatusChangedToCanceled_SavesStatusThenThrows() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setBalance(5000L);

        PaymentStatusEntity unpaidStatus = new PaymentStatusEntity();
        unpaidStatus.setId(1L);
        unpaidStatus.setName("unpaid");

        PaymentStatusEntity canceledStatus = new PaymentStatusEntity();
        canceledStatus.setId(3L);
        canceledStatus.setName("cancled");

        PaymentEntity payment = new PaymentEntity();
        payment.setId(1L);
        payment.setSid("cs_test_123");
        payment.setAmount(2000L);
        payment.setUser(user);
        payment.setStatus(unpaidStatus);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentStatusRepository.findByName("cancled")).thenReturn(Optional.of(canceledStatus));

        try (var sessionMock = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            sessionMock.when(() -> Session.retrieve("cs_test_123")).thenReturn(session);
            when(session.getPaymentStatus()).thenReturn("cancled");

            assertThrows(BadRequestException.class, () -> paymentService.handleSuccess(1L));

            assertEquals("cancled", payment.getStatus().getName(), "Status should be updated before throw");
            verify(paymentRepository).save(payment);
            verify(userRepository, never()).save(any());
        }
    }

    /*--reload--*/

    @Test
    void reload_IteratesUnpaidPayments() {
        PaymentStatusEntity unpaidStatus = new PaymentStatusEntity();
        unpaidStatus.setName("unpaid");

        PaymentEntity p1 = new PaymentEntity();
        p1.setId(1L);
        p1.setSid("cs_1");
        p1.setAmount(1000L);
        p1.setStatus(unpaidStatus);

        PaymentEntity p2 = new PaymentEntity();
        p2.setId(2L);
        p2.setSid("cs_2");
        p2.setAmount(500L);
        p2.setStatus(unpaidStatus);

        when(paymentRepository.findByStatusName("unpaid")).thenReturn(List.of(p1, p2));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(paymentRepository.findById(2L)).thenReturn(Optional.of(p2));

        try (var sessionMock = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            sessionMock.when(() -> Session.retrieve(anyString())).thenReturn(session);
            when(session.getPaymentStatus()).thenReturn("paid");

            PaymentStatusEntity paidStatus = new PaymentStatusEntity();
            paidStatus.setName("paid");
            when(paymentStatusRepository.findByName("paid")).thenReturn(Optional.of(paidStatus));

            UserEntity user = new UserEntity();
            user.setId(1L);
            user.setBalance(0L);
            p1.setUser(user);
            p2.setUser(user);

            paymentService.reload();

            verify(paymentRepository, times(2)).findById(any());
        }
    }

    @Test
    void reload_SkipsPaymentsThatThrow() {
        PaymentStatusEntity unpaidStatus = new PaymentStatusEntity();
        unpaidStatus.setName("unpaid");

        PaymentEntity p1 = new PaymentEntity();
        p1.setId(1L);
        p1.setSid("cs_1");
        p1.setStatus(unpaidStatus);

        when(paymentRepository.findByStatusName("unpaid")).thenReturn(List.of(p1));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p1));

        try (var sessionMock = mockStatic(Session.class)) {
            Session session = mock(Session.class);
            sessionMock.when(() -> Session.retrieve(anyString())).thenReturn(session);
            when(session.getPaymentStatus()).thenReturn("unpaid");

            paymentService.reload();

            verify(paymentRepository).findById(1L);
        }
    }

    /*--createPayment success--*/

    @Test
    void createPayment_Success() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("Jan");
        user.setSurname("Kowalski");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        PaymentStatusEntity unpaidStatus = new PaymentStatusEntity();
        unpaidStatus.setId(1L);
        unpaidStatus.setName("unpaid");
        when(paymentStatusRepository.findByName("unpaid")).thenReturn(Optional.of(unpaidStatus));

        Session session = mock(Session.class);
        when(session.getId()).thenReturn("cs_test_123");
        when(session.getUrl()).thenReturn("https://checkout.stripe.com/pay/test");

        var stripeClient = mock(com.stripe.StripeClient.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        when(stripeService.get()).thenReturn(stripeClient);
        when(stripeClient.v1().checkout().sessions().create(any(com.stripe.param.checkout.SessionCreateParams.class)))
                .thenReturn(session);

        try (var sessionMock = mockStatic(Session.class)) {
            Session paymentStatusSession = mock(Session.class);
            sessionMock.when(() -> Session.retrieve(anyString())).thenReturn(paymentStatusSession);
            when(paymentStatusSession.getPaymentStatus()).thenReturn("unpaid");

            var result = paymentService.createPayment(5000L, 1L);

            assertNotNull(result);
            assertEquals("https://checkout.stripe.com/pay/test", result.getUrl());

            verify(paymentRepository, times(2)).save(paymentCaptor.capture());
            PaymentEntity saved = paymentCaptor.getValue();
            assertEquals("cs_test_123", saved.getSid());
            assertEquals(1L, saved.getUser().getId());
            assertEquals(5000L, saved.getAmount());
        }
    }
}
