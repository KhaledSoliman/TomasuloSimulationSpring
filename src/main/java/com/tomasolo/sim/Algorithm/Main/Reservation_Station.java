
package com.tomasolo.sim.Algorithm.Main;


import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegFile;

import java.util.*;


public class Reservation_Station implements Iterable {
	private Map<String, Reservation_Station_Element[]> elements;
	private Reservation_Station_Element[] LW;
	private Reservation_Station_Element[] SW;
	private Reservation_Station_Element[] JMP_JALR_RET;
	private Reservation_Station_Element[] BEQ;
	private Reservation_Station_Element[] ADD_SUB_ADDI;
	private Reservation_Station_Element[] NAND;
	private Reservation_Station_Element[] MUL;

	private Map<String, Integer> counters;
	private int SW_counter = 0;
	private int LW_counter = 0;
	private int BEQ_counter = 0;
	private int JMP_JALR_RET_counter = 0;
	private int[] branch_imm = new int[2];
	private int ADD_SUB_ADDI_counter = 0;
	private int NAND_counter = 0;
	private int MUL_counter = 0;

	static String[] formats = {"LW", "SW", "JMP_JALR_RET", "BEQ", "ADD_SUB_ADDI", "NAND", "MUL"};

	Reservation_Station() {
		LW = new Reservation_Station_Element[2];
		for (int i = 0; i < 2; i++)
			LW[i] = new Reservation_Station_Element();


		SW = new Reservation_Station_Element[2];
		for (int i = 0; i < 2; i++)
			SW[i] = new Reservation_Station_Element();
		JMP_JALR_RET = new Reservation_Station_Element[3];
		for (int i = 0; i < 3; i++)
			JMP_JALR_RET[i] = new Reservation_Station_Element();
		BEQ = new Reservation_Station_Element[2];
		for (int i = 0; i < 2; i++)
			BEQ[i] = new Reservation_Station_Element();
		ADD_SUB_ADDI = new Reservation_Station_Element[3];
		for (int i = 0; i < 3; i++)
			ADD_SUB_ADDI[i] = new Reservation_Station_Element();
		NAND = new Reservation_Station_Element[1];
		for (int i = 0; i < 1; i++)
			NAND[i] = new Reservation_Station_Element();
		MUL = new Reservation_Station_Element[2];
		for (int i = 0; i < 2; i++)
			MUL[i] = new Reservation_Station_Element();

		elements = new HashMap<>();
		elements.put("LW", LW);
		elements.put("SW", SW);
		elements.put("JMP", JMP_JALR_RET);
		elements.put("JALR", JMP_JALR_RET);
		elements.put("RET", JMP_JALR_RET);
		elements.put("BEQ", BEQ);
		elements.put("ADD", ADD_SUB_ADDI);
		elements.put("SUB", ADD_SUB_ADDI);
		elements.put("ADDI", ADD_SUB_ADDI);
		elements.put("NAND", NAND);
		elements.put("MUL", MUL);
		counters = new HashMap<>();
	}

	void add(Instruction inst, ROB rob, int rob_ind, int PC) {
		Reservation_Station_Element[] x = elements.get(inst.getName());
		int y = empty_index(x);
		x[y].operation = inst.getName();
		x[y].busy = true;
		x[y].Vj = null;
		x[y].Qj = null;
		x[y].Vk = null;
		x[y].Qk = null;
		x[y].PC = PC;
		//SW LW JMP NO HERE
		int rob_indx = rob.find_dest(inst.getRegB(), x[y].rob_indx);
		if (rob_indx == -1) { //not found in ROB
			x[y].Vj = RegFile.read(inst.getRegB());
			x[y].Qj = null;
		} else if (rob.is_ready(rob_indx)) { //in ROB  // if rob entry is available not in queue
			x[y].Vj = rob.get_value(rob_indx);
			x[y].Qj = null;
		} else {
			x[y].Qj = rob_indx;
			x[y].Vj = null;
		}

		x[y].rob_indx = rob_ind;
		counters.put(inst.getName(), counters.get(inst.getName()) + 1);
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
			//type is combined for some instrs???
			runDupe1(elements.get(type)[k], CC, PC, PC2);
		} else {
			System.out.println("No Ready " + type + " Instructions !!");
		}

	}

	private void runDupe1(Reservation_Station_Element x, int CC, Integer PC, Integer PC2) {
		if (!x.PC.equals(PC) && !x.PC.equals(PC2) || (PC == null && PC2 == null)) {
			x.execution_start_cycle = CC;
			System.out.println("a " + x.operation + " is executing  ");
		}
	}

	private int empty_index(Reservation_Station_Element[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].busy)
				return i;
		}
		return -1;
	}

	public boolean check(Instruction instr) {
		int y;
		y = empty_index(elements.get(instr.getName()));
		return y != -1;
	}

	public void finish_execution(int CC, ROB rob) {
		Integer result;
		for (Map.Entry<String, Reservation_Station_Element[]> element : elements.entrySet()) {
			for (Reservation_Station_Element element2 : element.getValue()) {
				if (element2.execution_start_cycle != null) {
					if (CC - element2.execution_start_cycle >= 2) { //JMP_JAL_RET BEQ NAND >= 1 MUL >= 8
						if (element2.operation.equals(Instruction.JMP)) {
							result = element2.PC + element2.Vj;
							rob.set_value(element2.rob_indx, result, null); //write pc+imm to rob with dest pc
						} else if (element2.operation.equals(Instruction.JALR)) {
							result = element2.PC + 1;
							rob.set_value(element2.rob_indx, result, element2.Vj); //write pc+imm to rob with dest pc
							update(NAND[i].rob_indx, result); //update reservation station
						} else if (element2.operation.equals(Instruction.RET)) {
							result = element2.Vj;
							rob.set_value(element2.rob_indx, result, null); //write pc+imm to rob with dest pc
						}

						//BEQ
						result = execute(element2);
						if (result == 0) //branch taken
						{
							if (branch_imm[i] > 0) {
								result = branch_imm[i] + element2.PC; //store in branch pc pc+imm while adding !!!
								rob.set_value(element2.rob_indx, result, null);
							} else {
								result = null;
								rob.set_value(element2.rob_indx, result, null);
							}
						} else {
							if (branch_imm[i] < 0) {
								result = branch_imm[i] + element2.PC; //store in branch pc pc+imm while adding !!!
								rob.set_value(element2.rob_indx, result, null);
							} else {
								result = null;
								rob.set_value(element2.rob_indx, result, null);
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
						element2.rob_indx = null;
					}
				}

			}
		}
	}

	private Integer execute(Reservation_Station_Element rtrn) {
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
		Reservation_Station_Element[] x = elements.get(type);
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

	public int getNumExecutedInstructions() {
		return (LW_counter + SW_counter + JMP_JALR_RET_counter + BEQ_counter + ADD_SUB_ADDI_counter + NAND_counter + MUL_counter);
	}

	public int getNumBranchInstrs() {
		return BEQ_counter;
	}

	void update(Integer rob_indx, int result) {
		for (Map.Entry<String, Reservation_Station_Element[]> element : elements.entrySet()) {
			for (Reservation_Station_Element element2 : element.getValue()) {
				if (element2.Qj != null && element2.Qj.equals(rob_indx)) {
					element2.Vj = result;
				}
				if (element2.Qk != null && element2.Qk.equals(rob_indx)) {
					element2.Vk = result;
				}
			}
		}
	}

	@Override
	public Iterator<Reservation_Station_Element> iterator() {
		Iterator<Reservation_Station_Element> it;
		Reservation_Station_Element[] list =
				new Reservation_Station_Element[15];
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