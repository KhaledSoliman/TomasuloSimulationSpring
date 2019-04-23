package com.tomasolo.sim.Algorithm.ReservationStation;

import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.Rob.Rob;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegisterFile;

import java.util.*;


public class ReservationStation {
	private Map<String, ReservationStationGroup> groups;
	public static String[] formats= {"LW", "SW", "JMP", "BEQ", "ADD", "NAND","MUL"};

	public ReservationStation() {
		groups = new HashMap<>();
	}

	public void addGroup(String type, int numOfElements) {
		groups.put(type, new ReservationStationGroup(numOfElements));
	}

	public void addGroup(String[] types, int numOfElements) {
		ReservationStationGroup group = new ReservationStationGroup(numOfElements);
		for (String type : types) {
			groups.put(type, group);
		}
	}

	public void add(Instruction instruction, Rob rob, int robIndex, int PC) {
		ReservationStationElement[] x = groups.get(instruction.getName()).getElements();
		ReservationStationElement entry = x[getFree(x)];
		entry.setOperation(instruction.getName());
		entry.setBusy(true);
		entry.setPC(PC);
		entry.setRobIndex(robIndex);
		switch (entry.getOperation()) {
			case Instruction.SW:
			case Instruction.LW:
				entry.setAddress(instruction.getImmediate());
				break;
			case Instruction.ADDI:
				entry.setVk( instruction.getImmediate());
			case Instruction.ADD:
			case Instruction.SUB:
			case Instruction.NAND:
				this.testRob(entry, rob, robIndex, instruction.getRegB());
				this.testRob(entry, rob, robIndex, instruction.getRegC());
				break;
			case Instruction.BEQ:
				this.testRob(entry, rob, robIndex, instruction.getRegA());
				this.testRob(entry, rob, robIndex, instruction.getRegB());
				entry.setAddress(instruction.getImmediate());
				break;
			case Instruction.JALR:
			case Instruction.RET:
				this.testRob(entry, rob, robIndex, instruction.getRegA());
				break;
			case Instruction.JMP:
				entry.setVj( instruction.getImmediate());
				break;
			default:
				//Nothing MUL
		}
		System.out.println(instruction.getName() + " inst added!! ");
	}

	private void testRob(ReservationStationElement entry, Rob rob, int robIndex, int reg) {
		int robIndex2 = rob.findDestination(reg, robIndex);
		if (robIndex2 == -1) { //not found in Rob
			entry.setVj(RegisterFile.read(reg));
			entry.setQj(null);
		} else if (rob.isReady(robIndex2)) { //in Rob  // if rob entry is available not in queue
			entry.setVj(rob.getValue(robIndex2));
			entry.setQj(null);
		} else {
			entry.setVj(null);
			entry.setQj(robIndex2);
		}
	}

	public void remove(String type, Rob rob, int CC, Integer PC, Integer PC2) {
		//retrieves an inst with ready operands !!
		int k = getReady(type);
		if (k != -1) {
			ReservationStationElement x = groups.get(type).getElements()[k];
			if (!x.PC.equals(PC) && !x.PC.equals(PC2) || (PC == null && PC2 == null)) {
				x.execution_start_cycle = CC;
				System.out.println("a " + x.operation + " is executing  ");
			}
		} else {
			System.out.println("No Ready " + type + " Instructions !!");
		}
	}

	private int getFree(ReservationStationElement[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].isBusy())
				return i;
		}
		return -1;
	}

	public boolean isNotFull(Instruction instr) {
		int y;
		y = getFree(groups.get(instr.getName()).getElements());
		return y != -1;
	}

	public void finish_execution(int CC, Rob rob) {
		Integer result;
		for (Map.Entry<String, ReservationStationGroup> element : groups.entrySet()) {
			for (ReservationStationElement element2 : element.getValue().getElements()) {
				if (element2.getExecutionStartCycle() != null) {
					//current clock cycle - exec start >= 2
					if (CC - element2.getExecutionStartCycle() > 1) { //JMP_JAL_RET BEQ NAND >= 1 MUL >= 8
						result = element2.execute();
						//BEQ
						if (result == 0) //branch taken
						{
							if (branch_imm[i] > 0) {
								result = branch_imm[i] + element2.PC; //store in branch pc pc+immediate while adding !!!
							} else {
								result = null;
							}
						} else {
							if (branch_imm[i] < 0) {
								result = branch_imm[i] + element2.PC; //store in branch pc pc+immediate while adding !!!
							} else {
								result = null;
							}
						}
						rob.setValue(element2.getRobIndex(), result, element2.getOperation().equals(Instruction.JALR) ? element2.getVj() : null);
						if (result != null) update(element2.getRobIndex(), result);
						System.out.println(element2.getOperation() + "Is done Executing");
						element2.clear();
					}
				}
			}
		}
	}

	public void update(Integer robIndex, int result) {
		for (Map.Entry<String, ReservationStationGroup> group : groups.entrySet()) {
			for (ReservationStationElement element : group.getValue().getElements()) {
				element.setVj(element.getQj().equals(robIndex) ? result : element.getVj());
				element.setVk(element.getQk().equals(robIndex) ? result : element.getVk());
			}
		}
	}

	private int getReady(String type) {
		ReservationStationElement[] x = groups.get(type).getElements();
		for (int i = 0; i < x.length; i++) {
			if (x[i].getOperation() != null && x[i].isBusy() && x[i].getExecutionStartCycle() == null)
				return i;
		}
		return -1;
	}

	public int countExecutedInstructions() {
		int sum = 0;
		for (Map.Entry<String, ReservationStationGroup> group : groups.entrySet())
			sum += group.getValue().getCounter();
		return sum;
	}

	public int countBranchInstructions() {
		return groups.get("BEQ").getCounter();
	}


}

	/*

	@Override
	public Iterator<ReservationStationElement> iterator() {
		Iterator<ReservationStationElement> it;
		ReservationStationElement[] list =
				new ReservationStationElement[15];
		System.arraycopy(LW, 0, list, 0, LW.length);
		System.arraycopy(SW, 0, list, LW.length, SW.length);
		System.arraycopy(JMP_JALR_RET, 0, list, LW.length + SW.length, JMP_JALR_RET.length);
		System.arraycopy(BEQ, 0, list, LW.length + SW.length + JMP_JALR_RET.length, BEQ.length);
		System.arraycopy(ADD_SUB_ADDI, 0, list, LW.length + SW.length + JMP_JALR_RET.length + BEQ.length, ADD_SUB_ADDI.length);
		System.arraycopy(NAND, 0, list, LW.length + SW.length + JMP_JALR_RET.length + BEQ.length + ADD_SUB_ADDI.length, NAND.length);
		System.arraycopy(MUL, 0, list, LW.length + SW.length + JMP_JALR_RET.length + BEQ.length + ADD_SUB_ADDI.length + NAND.length, MUL.length);
		it = Arrays.asList(list).iterator();
		return it;
	}

		String obj;
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		Scanner in = new Scanner(System.in);
		//System.out.println("Enter Reservation Station Name");
		//obj = in.nextLine();
		switch (obj) {
			case "LW": {
				it = Arrays.asList(LW).iterator();
				break;
			}
			case "SW": {
				it = Arrays.asList(SW).iterator();
				break;
			}
			case "JMP_JALR_RET": {
				it = Arrays.asList(JMP_JALR_RET).iterator();
				break;
			}
			case "BEQ": {
				it = Arrays.asList(BEQ).iterator();
				break;
			}
			case "ADD_SUB_ADDI": {
				it = Arrays.asList(ADD_SUB_ADDI).iterator();
				break;
			}
			case "NAND": {
				it = Arrays.asList(NAND).iterator();
				break;
			}
			case "MUL": {
				it = Arrays.asList(MUL).iterator();
				break;
			}
			default: {
				it = null;
				break;
			}

		}
		*/