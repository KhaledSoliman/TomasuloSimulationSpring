package com.tomasolo.sim.Algorithm.ReservationStation;

import com.tomasolo.sim.Algorithm.Instruction.Instruction;


public class ReservationStationElement {
	/**
	 * Operation to perform in the unit
	 */
	private String operation;
	/**
	 * Value of Source operands
	 */
	private Integer Vj, Vk;
	/**
	 * Reservation stations producing source registers (value to be written)
	 */
	private Integer Qj, Qk;
	/**
	 * Indicates reservation station or functional unit is busy
	 */
	private boolean busy;
	/**
	 * Address information; initially contains the immediate value; then the full address
	 */
	private Integer address;

	private Integer PC;
	private Integer robIndex;
	private Integer executionStartCycle;


	ReservationStationElement() {
		this.clear();
	}

	void clear() {
		PC = null;
		executionStartCycle = null;
		robIndex = null;
		operation = null;
		busy = false;
		Vk = null;
		Vj = null;
		Qk = null;
		Qj = null;
		address = null;
	}

	public Integer execute() {
		Integer result;
		switch (operation) {
			case Instruction.ADD:
			case Instruction.ADDI:
				result = Vj + Vk;
				break;
			case Instruction.SUB:
			case Instruction.BEQ:
				result = Vj - Vk;
				break;
			case Instruction.NAND:
				result = ~(Vj & Vk);
				break;
			case Instruction.JALR:
				result = PC + 1;
				break;
			case Instruction.JMP:
				result = PC + Vj;
				break;
			case Instruction.RET:
				result = Vj;
				break;
			case Instruction.MUL:
				result = Vj * Vk;
				break;
			default:
				result = null;
		}
		return result;
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
		row[7] = executionStartCycle != null ? Integer.toString(executionStartCycle) : "";
		return row;
	}


	public Integer getPC() {
		return PC;
	}

	public void setPC(Integer PC) {
		this.PC = PC;
	}

	public Integer getRobIndex() {
		return robIndex;
	}

	public void setRobIndex(Integer robIndex) {
		this.robIndex = robIndex;
	}

	public Integer getExecutionStartCycle() {
		return executionStartCycle;
	}

	public void setExecutionStartCycle(Integer executionStartCycle) {
		this.executionStartCycle = executionStartCycle;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public Integer getVk() {
		return Vk;
	}

	public void setVk(Integer vk) {
		Vk = vk;
	}

	public Integer getVj() {
		return Vj;
	}

	public void setVj(Integer vj) {
		Vj = vj;
	}

	public Integer getQk() {
		return Qk;
	}

	public void setQk(Integer qk) {
		Qk = qk;
	}

	public Integer getQj() {
		return Qj;
	}

	public void setQj(Integer qj) {
		Qj = qj;
	}

	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
	}
}

