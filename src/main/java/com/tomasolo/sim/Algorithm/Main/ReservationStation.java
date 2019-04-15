package com.tomasolo.sim.Algorithm.Main;

import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegFile;

import java.util.*;


public class ReservationStation implements Iterable {
	private Map<String, ReservationStationGroup> groups;
	private int[] branch_imm = new int[2];

	static String[] formats = {"LW", "SW", "JMP", "JALR", "RET", "BEQ", "ADD", "SUB", "ADDI", "NAND", "MUL"};

	ReservationStation() {
		groups = new HashMap<>();
		groups.put("LW", new ReservationStationGroup(2));
		groups.put("SW", new ReservationStationGroup(2));
		ReservationStationGroup JMP_JALR_RET = new ReservationStationGroup(3);
		groups.put("JMP", JMP_JALR_RET);
		groups.put("JALR", JMP_JALR_RET);
		groups.put("RET", JMP_JALR_RET);
		groups.put("BEQ", new ReservationStationGroup(2));
		ReservationStationGroup ADD_SUB_ADDI = new ReservationStationGroup(3);
		groups.put("ADD", ADD_SUB_ADDI);
		groups.put("SUB", ADD_SUB_ADDI);
		groups.put("ADDI", ADD_SUB_ADDI);
		groups.put("NAND", new ReservationStationGroup(1));
		groups.put("MUL", new ReservationStationGroup(3));
	}

	void add(Instruction inst, ROB rob, int robIndex, int PC) {
		ReservationStationElement[] x = groups.get(inst.getName()).getElements();
		int y = empty_index(x);
		x[y].operation = inst.getName();
		x[y].busy = true;
		x[y].Vj = null;
		x[y].Qj = null;
		x[y].Vk = null;
		x[y].Qk = null;
		x[y].PC = PC;
		x[y].robIndex = robIndex;
		//SW LW JMP NO HERE
		int robIndex2 = rob.find_dest(inst.getRegB(), x[y].robIndex);
		if (robIndex2 == -1) { //not found in ROB
			x[y].Vj = RegFile.read(inst.getRegB());
			x[y].Qj = null;
		} else if (rob.is_ready(robIndex2)) { //in ROB  // if rob entry is available not in queue
			x[y].Vj = rob.get_value(robIndex2);
			x[y].Qj = null;
		} else {
			x[y].Qj = robIndex2;
			x[y].Vj = null;
		}
		groups.get(inst.getName()).incrementCounter();
		System.out.println(inst.getName() + " inst added!! ");
		//JMP
		JMP_JALR_RET[y].Vj = inst.getImm();
		//RegA	//JALR vj qj
		//RET vj qj
		//RegAB  //BEQ
		branch_imm[y] = inst.getImm();
		//RegBC //ADD // Sub //NAND vj qj vk qk
		// ADDI
		ADD_SUB_ADDI[y].Vk = inst.getImm();
		//Mul Nothing?
		//Default
		System.out.println(inst.getName() + " failed to add ");

	}

	void remove(String type, ROB rob, int CC, Integer PC, Integer PC2) {
		//retrieves an inst with ready operands !!
		int k = get_ready(type);
		if (k != -1) {
			//type is combined for some instrs??? => formats
			ReservationStationElement x = groups.get(type).getElements()[k];
			if (!x.PC.equals(PC) && !x.PC.equals(PC2) || (PC == null && PC2 == null)) {
				x.execution_start_cycle = CC;
				System.out.println("a " + x.operation + " is executing  ");
			}
		} else {
			System.out.println("No Ready " + type + " Instructions !!");
		}

	}

	private int empty_index(ReservationStationElement[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].busy)
				return i;
		}
		return -1;
	}

	boolean check(Instruction instr) {
		int y;
		y = empty_index(groups.get(instr.getName()).getElements());
		return y != -1;
	}

	void finish_execution(int CC, ROB rob) {
		Integer result;
		for (Map.Entry<String, ReservationStationGroup> element : groups.entrySet()) {
			for (ReservationStationElement element2 : element.getValue().getElements()) {
				if (element2.execution_start_cycle != null) {
					if (CC - element2.execution_start_cycle >= 2) { //JMP_JAL_RET BEQ NAND >= 1 MUL >= 8
						if (element2.operation.equals(Instruction.JMP)) {
							result = element2.PC + element2.Vj;
							rob.set_value(element2.robIndex, result, null); //write pc+imm to rob with dest pc
						} else if (element2.operation.equals(Instruction.JALR)) {
							result = element2.PC + 1;
							rob.set_value(element2.robIndex, result, element2.Vj); //write pc+imm to rob with dest pc
							update(NAND[i].rob_indx, result); //update reservation station
						} else if (element2.operation.equals(Instruction.RET)) {
							result = element2.Vj;
							rob.set_value(element2.robIndex, result, null); //write pc+imm to rob with dest pc
						}

						//BEQ
						result = execute(element2);
						if (result == 0) //branch taken
						{
							if (branch_imm[i] > 0) {
								result = branch_imm[i] + element2.PC; //store in branch pc pc+imm while adding !!!
								rob.set_value(element2.robIndex, result, null);
							} else {
								result = null;
								rob.set_value(element2.robIndex, result, null);
							}
						} else {
							if (branch_imm[i] < 0) {
								result = branch_imm[i] + element2.PC; //store in branch pc pc+imm while adding !!!
								rob.set_value(element2.robIndex, result, null);
							} else {
								result = null;
								rob.set_value(element2.robIndex, result, null);
							}
						}

						//NAND
						System.out.println(NAND[i].operation + "Is done Executing");
						result = execute(NAND[i]);
						rob.set_value(NAND[i].rob_indx, result, null);
						update(NAND[i].rob_indx, result);


						//ADD_SUB_ADDI
						System.out.println(ADD_SUB_ADDI[i].operation + "Is done Executing");
						result = execute(ADD_SUB_ADDI[i]);
						rob.set_value(ADD_SUB_ADDI[i].rob_indx, result, null);
						update(ADD_SUB_ADDI[i].rob_indx, result);

						//MUL
						System.out.println(MUL[i].operation + "Is done Executing");
						result = execute(MUL[i]);
						rob.set_value(MUL[i].rob_indx, result, null);
						update(MUL[i].rob_indx, result);


						element2.Qj = null;
						element2.Qk = null;
						element2.Vj = null;
						element2.Vk = null;
						element2.busy = false;
						element2.execution_start_cycle = null;
						element2.PC = null;
						element2.operation = null;
						element2.robIndex = null;
					}
				}

			}
		}
	}

	private Integer execute(ReservationStationElement rtrn) {
		Integer result;
		switch (rtrn.operation) {
			case Instruction.ADD:
			case Instruction.ADDI:
				result = rtrn.Vj + rtrn.Vk;
				break;
			case Instruction.SUB:
			case Instruction.BEQ:
				result = rtrn.Vj - rtrn.Vk;
				break;
			case Instruction.MUL:
				result = rtrn.Vj * rtrn.Vk;
				break;
			case Instruction.NAND:
				result = ~(rtrn.Vj & rtrn.Vk);
				break;
			default:
				result = null;
		}
		return result;
	}

	private int get_ready(String type) {
		//type is combined instrs
		ReservationStationElement[] x = groups.get(type).getElements();
		for (int i = 0; i < x.length; i++) {
			//always ready LW, SW
			if (x[i].operation != null && x[i].busy && x[i].execution_start_cycle == null)
				return i;
			//always ready JMP, JALR, RET
			if (x[i].operation != null && x[i].busy && x[i].Vj != null && x[i].execution_start_cycle == null)
				return i;
			//always ready BEQ, ADD, SUB, ADDI, NAND, MUL
			if (x[i].operation != null && x[i].Vj != null && x[i].Vk != null && x[i].busy && x[i].execution_start_cycle == null)
				return i;
		}
		return -1;
	}

	int getNumExecutedInstructions() {
		int sum = 0;
		for (Map.Entry<String, ReservationStationGroup> group : groups.entrySet())
			sum += group.getValue().getCounter();
		return sum;
	}

	int getNumBranchInstrs() {
		return groups.get("BEQ").getCounter();
	}

	void update(Integer robIndex, int result) {
		for (Map.Entry<String, ReservationStationGroup> group : groups.entrySet()) {
			for (ReservationStationElement element : group.getValue().getElements()) {
				element.Vj = element.Qj != null && element.Qj.equals(robIndex) ? result : element.Vj;
				element.Vk = element.Qk != null && element.Qk.equals(robIndex) ? result : element.Vk;
			}
		}
	}

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

}

	/*

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