package com.tcs.transactionService;

import org.springframework.context.ApplicationEvent;

public class StartTransactionEvent extends ApplicationEvent {
	public StartTransactionEvent(Object source) {
		super(source);
	}
}
