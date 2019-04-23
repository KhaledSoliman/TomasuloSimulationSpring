package com.tomasolo.sim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import lombok.Data;

@Data
public class POJO {
	private Instruction[] instructions;
	private DataItemPOJO[] dataItems;
	private Integer startingAddress;

	public Instruction[] getInstructions() {
		return instructions;
	}

	@JsonCreator
	public POJO (@JsonProperty(value = "instructions") Instruction[] instructions,
	             @JsonProperty(value = "dataItems") DataItemPOJO[] dataItems,
	             @JsonProperty(value = "startingAddress") Integer startingAddress) {
		this.instructions = instructions;
		this.dataItems = dataItems;
		this.startingAddress = startingAddress;
	}
}
