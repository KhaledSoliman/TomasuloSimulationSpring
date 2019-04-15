
package com.tomasolo.sim.Algorithm.Main;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ROB_NODE {
	Integer index;
	String type;
	Integer dest;
	boolean ready;
	Integer value;
	Integer jalr_value2;
	@JsonIgnore
	ROB_NODE next;
	@JsonIgnore
	ROB_NODE previous;

	public String toString() {
		return index + ' ' + type + ' ' + dest + ' ' + ready + ' ' + value;
	}
}
