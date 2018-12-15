package com.tomasolo.sim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class StartingAddressPOJO {
	Integer startingAddress;

	 StartingAddressPOJO(Integer startingAddress) {
		this.startingAddress = startingAddress;
	}
}
