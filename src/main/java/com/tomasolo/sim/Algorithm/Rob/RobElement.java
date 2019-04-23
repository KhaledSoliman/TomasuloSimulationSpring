package com.tomasolo.sim.Algorithm.Rob;

import lombok.Data;

@Data
public class RobElement {
	private String operation;
	private Integer destination, value, jalrValue;
	private boolean ready, branch;

	RobElement(String operation, Integer destination, Integer value, Integer jalrValue, boolean ready, boolean branch) {
		this.operation = operation;
		this.destination = destination;
		this.value = value;
		this.jalrValue = jalrValue;
		this.ready = ready;
		this.branch = branch;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Integer getDestination() {
		return destination;
	}

	public void setDestination(Integer destination) {
		this.destination = destination;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getJalrValue() {
		return jalrValue;
	}

	public void setJalrValue(Integer jalrValue) {
		this.jalrValue = jalrValue;
	}

	public String toString() {
		return String.format("operation = %s, destination = %d, ready = %b, value = %d, branch = %b, jalrVal = %d", operation, destination,
				ready, value, branch, jalrValue);
	}

}
