package com.pewniaczekbet.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import com.pewniaczekbet.model.dao.PaymentRepository;
import com.pewniaczekbet.model.dao.PaymentStatusRepository;
import com.pewniaczekbet.model.dao.UserRepository;
import com.pewniaczekbet.model.entities.PaymentEntity;
import com.pewniaczekbet.model.entities.PaymentStatusEntity;
import com.pewniaczekbet.model.entities.UserEntity;
import com.pewniaczekbet.model.exceptions.BadRequestException;
import com.pewniaczekbet.model.exceptions.InternalServerErrorException;
import com.pewniaczekbet.other.ApplicationLimitations;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * PaymentService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final UserRepository userRepository;
	private final PaymentStatusRepository paymentStatusRepository;
	private final PaymentRepository paymentRepository;
	private final StripeService stripeService;

	@Value("${STRIPE_PUBLIC_KEY}")
	private String stripePublicKey;

	@Value("${STRIPE_REDIRECT_SUCCESS}")
	private String stripeRedirectSuccess;

	@Value("${STRIPE_REDIRECT_CANCEL}")
	private String stripeRedirectCancel;

	@Value("${STRIPE_REDIRECT_FINAL}")
	private String stripeFinalRedirect;

	@Value("${STRIPE_REDIRECT_FINAL_BAD}")
	private String stripeFinalRedirectBad;

	private String getPaymentStatus(String paymentId) {
		try {
			Session intent = Session.retrieve(paymentId);
			return intent.getPaymentStatus();
		} catch (StripeException e) {
			// e.printStackTrace();
			throw new BadRequestException("unable to find payment");
		}
	}

	public void reload() {
		List<PaymentEntity> payments = paymentRepository.findByStatusName("unpaid");
		for (PaymentEntity x : payments) {
			try {
				handleSuccess(x.getId());
				System.out.println("changed");
			} catch (BadRequestException e) {

			}
		}
	}

	@Transactional
	public RedirectView handleSuccess(Long paymentId) {
		PaymentEntity entity = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new BadRequestException("unable to find payment"));
		String payment = getPaymentStatus(entity.getSid());
		if (!payment.equals(entity.getStatus().getName())) {
			if (payment.equals("paid")) {
				PaymentStatusEntity status = paymentStatusRepository.findByName(payment)
						.orElseThrow(() -> new InternalServerErrorException("unable to find payment status"));
				entity.setStatus(status);
				entity.getUser().setBalance(entity.getUser().getBalance() + entity.getAmount());
				userRepository.save(entity.getUser());
				paymentRepository.save(entity);
				return new RedirectView(stripeFinalRedirect);
			} else if (payment.equals("cancled")) {
				PaymentStatusEntity status = paymentStatusRepository.findByName("cancled")
						.orElseThrow(() -> new InternalServerErrorException("unable to find payment status"));
				entity.setStatus(status);
				paymentRepository.save(entity);
			}
		}
		return new RedirectView(stripeFinalRedirectBad);
	}

	public RedirectView createPayment(Long amount, Long userId) {

		if (amount < ApplicationLimitations.MinPayment || amount > ApplicationLimitations.MaxPayment)
			throw new BadRequestException("Bad payment amount, expected <" + ApplicationLimitations.MinPayment + ","
					+ ApplicationLimitations.MaxPayment + ">");

		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("dead session"));

		String reminder = String.valueOf(amount % 100);
		if (reminder.length() == 1)
			reminder += "0";
		String description = "Balance increase of " + (amount / 100) + "." + reminder
				+ " PLN for " + user.getName() + " " + user.getSurname();

		PaymentEntity payment = new PaymentEntity();
		paymentRepository.save(payment);

		StripeClient stripeClient = stripeService.get();
		SessionCreateParams params = new SessionCreateParams.Builder()
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(stripeRedirectSuccess + "?paymentId=" + payment.getId())
				.setCancelUrl(stripeRedirectCancel + "?paymentId=" + payment.getId())
				.addLineItem(
						SessionCreateParams.LineItem.builder()
								.setQuantity(1L)
								.setPriceData(
										SessionCreateParams.LineItem.PriceData.builder()
												.setCurrency("pln")
												.setProductData(
														SessionCreateParams.LineItem.PriceData.ProductData.builder()
																.setDescription(description)
																.setName("Add funds")
																.addImage("https://i.imgur.com/8GrBf4f.jpg")
																.build())
												.setUnitAmount(amount)
												.build())
								.build())
				.build();
		try {
			Session session = stripeClient.v1().checkout().sessions().create(params);
			session.getId();

			RedirectView view = new RedirectView();
			view.setUrl(session.getUrl());

			payment.setSid(session.getId());
			payment.setDescription(description);
			payment.setAmount(amount);
			payment.setUser(user);
			payment.setPaymentDate(LocalDateTime.now());
			payment.setStatus(paymentStatusRepository.findByName("unpaid")
					.orElseThrow(() -> new InternalServerErrorException("unable to find payment status")));
			paymentRepository.save(payment);

			System.out.println(getPaymentStatus(payment.getSid()));
			return view;
		} catch (StripeException e) {
			throw new InternalServerErrorException(e.toString());
		}
	}
}
