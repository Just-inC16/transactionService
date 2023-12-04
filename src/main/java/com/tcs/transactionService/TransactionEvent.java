package com.tcs.transactionService;

public class TransactionEvent {
	private String type;
	private CustomerOrder order;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CustomerOrder getOrder() {
		return order;
	}

	public void setOrder(CustomerOrder order) {
		this.order = order;
	}

	public String toString() {
		return "Event: " + type + " " + order;
	}
}