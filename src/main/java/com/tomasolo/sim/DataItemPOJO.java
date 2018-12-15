package com.tomasolo.sim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class DataItemPOJO {
	private Integer address, values;

	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
	}

	public Integer getValues() {
		return values;
	}

	public void setValues(Integer values) {
		this.values = values;
	}

	@JsonCreator
	public DataItemPOJO(@JsonProperty(value = "address") Integer address,@JsonProperty(value = "values") Integer values) {
		this.address = address;
		this.values = values;
	}
}
