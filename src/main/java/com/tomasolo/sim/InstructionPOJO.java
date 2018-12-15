package com.tomasolo.sim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class InstructionPOJO {
	private String[] operands;
	private String name;

	public String[] getOperands() {
		return operands;
	}

	public void setOperands(String[] operands) {
		this.operands = operands;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonCreator
	public InstructionPOJO(@JsonProperty(value = "operands") String[] operands, @JsonProperty(value = "name") String name) {
		this.operands = operands;
		this.name = name;
	}
}
