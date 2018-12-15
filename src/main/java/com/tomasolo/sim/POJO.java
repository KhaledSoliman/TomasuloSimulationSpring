package com.tomasolo.sim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class POJO {
	private InstructionPOJO[] instructions;
	private DataItemPOJO[] dataItems;
	private Integer startingAddress;

	public InstructionPOJO[] getInstructions() {
		return instructions;
	}

	public void setInstructions(InstructionPOJO[] instructions) {
		this.instructions = instructions;
	}

	public DataItemPOJO[] getDataItems() {
		return dataItems;
	}

	public void setDataItems(DataItemPOJO[] dataItems) {
		this.dataItems = dataItems;
	}

	public Integer getStartingAddress() {
		return startingAddress;
	}

	public void setStartingAddress(Integer startingAddress) {
		this.startingAddress = startingAddress;
	}

	@JsonCreator
	public POJO (@JsonProperty(value = "instructions") InstructionPOJO[] instructions,
	             @JsonProperty(value = "dataItems") DataItemPOJO[] dataItems,
	             @JsonProperty(value = "startingAddress") Integer startingAddress) {
		this.instructions = instructions;
		this.dataItems = dataItems;
		this.startingAddress = startingAddress;
	}
}
