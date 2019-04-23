package com.tomasolo.sim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataItemPOJO {
	private Integer address, values;

	@JsonCreator
	public DataItemPOJO(@JsonProperty(value = "address") Integer address, @JsonProperty(value = "values") Integer values) {
		this.address = address;
		this.values = values;
	}
}
