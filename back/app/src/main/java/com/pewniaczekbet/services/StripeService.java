package com.pewniaczekbet.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.StripeClient;

/**
 * StripeService
 */
@Service
public class StripeService {

	private final StripeClient client;

	public StripeService(
			@Value("${STRIPE_SECRET_KEY}") String stripePrivateKey) {
		this.client = new StripeClient(stripePrivateKey);
		Stripe.apiKey = stripePrivateKey;
	}

	public StripeClient get() {
		return client;
	}
}
