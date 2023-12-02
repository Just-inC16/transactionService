package com.tcs.transactionService;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {
	@EventListener
	public void handleStartTransactionEvent(StartTransactionEvent event) {
		// Logic to handle the event
		System.out.println("Event Handled:" + event);
	}
}