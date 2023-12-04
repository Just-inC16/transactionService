package com.tcs.transactionService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
		Transaction newTransaction = new Transaction();
		try {
			// Save the transaction into db

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
			// If error, set the status for transaction to failure
			// Note: You don't need to send event b/c it failed at the beginning and it's
			// noted(FAILURE)!
			newTransaction.setStatus(Status.FAILURE);
			this.transactionRepository.save(newTransaction);
		}
	}

	@KafkaListener(topics = "reversed-transaction")
	public String reverseTransaction(String event) throws JsonMappingException, JsonProcessingException {
		System.out.println("We need to reverse transaction" + event);
		return "We need to reverse transaction";
	}

	@KafkaListener(topics = "success-transaction")
	public void successTransaction(String event) throws JsonMappingException, JsonProcessingException {
		System.out.println("successful transaction so edit status to Success");
	}

}
