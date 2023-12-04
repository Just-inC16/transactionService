package com.tcs.transactionService;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	private TransactionRepository transactionRepository;
	private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

	public TransactionController(TransactionRepository transactionRepository,
			KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
		this.transactionRepository = transactionRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	@GetMapping
	public String getHelloWorld() {
		return "Hello World :-)";
	}

	@PostMapping
	public void createTransaction(@RequestBody CustomerOrder customerOrder) {
		try {
			// Save the transaction into db
			Transaction newTransaction = new Transaction();
			newTransaction.setSenderId(customerOrder.getSenderId());
			newTransaction.setReceiverId(customerOrder.getReceiverId());
			newTransaction.setAmount(customerOrder.getAmount());
			newTransaction.setStatus(Status.PENDING);
			newTransaction = this.transactionRepository.save(newTransaction);

			// Set the new id (from newTransaction to customerOrder)
			customerOrder.setTransactionId(newTransaction.getId());

			// publish event for account microservice to consume
			// Note: new-orders is a listener name
			TransactionEvent event = new TransactionEvent();
			event.setOrder(customerOrder);
			event.setType("TRANSACTION_CREATED");
			System.out.println(event.toString());
			this.kafkaTemplate.send("new-orders", event);
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
	}
}
