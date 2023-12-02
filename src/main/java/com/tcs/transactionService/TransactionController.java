package com.tcs.transactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private EventPublisher eventPublisher;

	@PostMapping("/initiate")
	public Transaction initiateTransaction(@RequestBody Transaction transaction) {
		transaction.setStatus(Status.PENDING);
		transactionRepository.save(transaction);
		eventPublisher.publishStartTransactionEvent(new StartTransactionEvent(transaction.getId()));
		return transaction;
	}
}
