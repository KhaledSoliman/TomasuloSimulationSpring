
package com.tomasolo.sim.Algorithm.Main;


public class ReservationStationElement {
	String operation;
	boolean busy;
	Integer Vk;
	Integer Vj;
	Integer Qk;
	Integer PC;
	Integer Qj;
	Integer robIndex;
	Integer execution_start_cycle;

	ReservationStationElement() {
		PC = null;
		execution_start_cycle = null;
		operation = null;
		busy = false;
		Vk = null;
		Vj = null;
		Qk = null;
		Qj = null;
		robIndex = null;
	}

	public String toString() {
		return operation + ' ' + busy + ' ' + Vj + ' ' + Vk + ' ' + Qj + ' ' + Qk + ' ' + robIndex;
	}

	public String[] toArray() {
		String[] row = new String[8];
		row[0] = Boolean.toString(busy);
		row[1] = operation;
		row[2] = Vk != null ? Integer.toString(Vk) : "";
		row[3] = Vj != null ? Integer.toString(Vj) : "";
		row[4] = Qk != null ? Integer.toString(Qk) : "";
		row[5] = Qj != null ? Integer.toString(Qj) : "";
		row[6] = robIndex != null ? Integer.toString(robIndex) : "";
		row[7] = execution_start_cycle != null ? Integer.toString(execution_start_cycle) : "";
		return row;
	}


}
