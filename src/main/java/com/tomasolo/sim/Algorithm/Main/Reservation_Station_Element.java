
package com.tomasolo.sim.Algorithm.Main;


public class Reservation_Station_Element {
	String operation;
	boolean busy;
	Integer Vk;
	Integer Vj;
	Integer Qk;
	Integer PC;
	Integer Qj;
	Integer rob_indx;
	Integer execution_start_cycle;

	Reservation_Station_Element() {
		PC = null;
		execution_start_cycle = null;
		operation = null;
		busy = false;
		Vk = null;
		Vj = null;
		Qk = null;
		Qj = null;
		rob_indx = null;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();

		s.append(operation);
		s.append(' ');
		s.append(busy);
		s.append(' ');
		s.append(Vj);
		s.append(' ');
		s.append(Vk);
		s.append(' ');
		s.append(Qj);
		s.append(' ');
		s.append(Qk);
		s.append(' ');
		s.append(rob_indx);


		return s.toString();
	}

	public String[] toArray() {
		String[] row = new String[8];
		row[0] = Boolean.toString(busy);
		row[1] = operation;
		row[2] = Vk != null ? Integer.toString(Vk) : "";
		row[3] = Vj != null ? Integer.toString(Vj) : "";
		row[4] = Qk != null ? Integer.toString(Qk) : "";
		row[5] = Qj != null ? Integer.toString(Qj) : "";
		row[6] = rob_indx != null ? Integer.toString(rob_indx) : "";
		row[7] = execution_start_cycle != null ? Integer.toString(execution_start_cycle) : "";
		return row;
	}


}
