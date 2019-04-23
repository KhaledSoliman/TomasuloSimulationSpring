package com.tomasolo.sim.Algorithm.Main;

import com.tomasolo.sim.Algorithm.Instruction.*;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.*;
import com.tomasolo.sim.Algorithm.ReservationStation.ReservationStation;
import com.tomasolo.sim.Algorithm.Rob.Rob;

import java.util.ArrayList;

public class Controller implements LoadBuffer.MemoryInterface, Main.ClkInterface {
	private ArrayList<Instruction> instructionsList;
	private LoadBuffer loadBuffer;
	private Memory memory;
	private Integer pcPrediction;
	private Instruction prevInstruction;

	InstructionQueue instructionQueue;
	Rob rob;
	ReservationStation reservationStation;
	int missPredictionCount;

	public static int awaitingInstructionIndex;

	/**
	 * Constructor
	 *
	 * @param instructions list of instructions to execute
	 */
	public Controller(ArrayList<Instruction> instructions) {
		loadBuffer = new LoadBuffer(this);
		memory = new Memory();
		rob = new Rob();
		reservationStation = new ReservationStation();
		reservationStation.addGroup(Instruction.LW, 2);
		reservationStation.addGroup(Instruction.SW, 2);
		reservationStation.addGroup(Instruction.BEQ, 2);
		reservationStation.addGroup(Instruction.NAND, 1);
		reservationStation.addGroup(Instruction.MUL, 3);
		reservationStation.addGroup(new String[]{Instruction.JMP, Instruction.JALR, Instruction.JALR}, 3);
		reservationStation.addGroup(new String[]{Instruction.ADD, Instruction.SUB, Instruction.ADDI}, 3);

		instructionsList = new ArrayList<>();
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instr = instructions.get(i);
			instr.setPc(i);
			instructionsList.add(instr);
		}

		instructionQueue = new InstructionQueue();
		for (int i = 0; i < instructionsList.size(); i++) {
			boolean enqueued = instructionQueue.enqueue(instructionsList.get(i));
			if (!enqueued) {
				awaitingInstructionIndex = i;
				break;
			}
		}

		pcPrediction = 0;
		prevInstruction = null;
		missPredictionCount = 0;
	}


	@Override
	public int load(int address) {
		try {
			return memory.read(address);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public boolean memoryLoadDone(int robIndex, int data) {
		reservationStation.update(robIndex, data);
		return rob.setValue(robIndex, data, null);
	}

	@Override
	public boolean store(int robIndex, int address, int data) {
		try {
			return rob.setValue(robIndex, data, address);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * equivalent to always @ posedge clk in verilog
	 *
	 * @param CC current clock cycle
	 */
	@Override
	public void didUpdate(int CC) {
		System.out.println("Cycle " + CC);
		Integer pcIn;
		Instruction instr1;

		Instruction[] deqIns = new Instruction[2];
		for (int i = 0; i < 2; i++) {
			deqIns[i] = null;
		}
		for (int i = 0; i < 2; i++) {
			instr1 = instructionQueue.peek();
			if (instr1 != null) {
				if (instr1.getPc() != pcPrediction) {
					if (prevInstruction != null && prevInstruction.getName().equals(Instruction.BEQ)) {
						refillQueue(pcPrediction);
						missPredictionCount++;
						System.out.println("Miss Prediction " + instr1.getName());
					}
				}
				if (rob.isNotFull() && reservationStation.isNotFull(instr1)) {
					if (instr1.getName().equals(Instruction.LW) || instr1.getName().equals(Instruction.SW)) {
						if (instr1.getName().equals(Instruction.LW) && loadBuffer.loadIsFree()) {
							deqIns[i] = instructionQueue.dequeue(instructionsList, awaitingInstructionIndex);
							if (instructionQueue.peek() != null && instructionQueue.peek().getName().equals(Instruction.BEQ))
								prevInstruction = deqIns[i];
							loadBufferLogic(deqIns[i]);
						} else if (instr1.getName().equals(Instruction.SW) && loadBuffer.storeIsFree()) {
							deqIns[i] = instructionQueue.dequeue(instructionsList, awaitingInstructionIndex);
							if (instructionQueue.peek() != null && instructionQueue.peek().getName().equals(Instruction.BEQ))
								prevInstruction = deqIns[i];
							loadBufferLogic(deqIns[i]);
						}
						//else wait
					} else {
						//System.out.println("before fetch " + instructionQueue.peek().getName());
						deqIns[i] = instructionQueue.dequeue(instructionsList, awaitingInstructionIndex);
						if (instructionQueue.peek() != null && instructionQueue.peek().getName().equals(Instruction.BEQ))
							prevInstruction = deqIns[i];

						if (deqIns[i].getName().equals(Instruction.BEQ)) {
							if (deqIns[i].getImmediate() < 0)
								pcPrediction = deqIns[i].getPc() + deqIns[i].getImmediate();
							else
								pcPrediction = deqIns[i].getPc() + 1;
						} else
							pcPrediction = deqIns[i].getPc() + 1;
						fetch(deqIns[i]);
					}
				}
			}

			/*
			 * Execute and Write Back
			 */
			//nextCycle(CC);
		}

		execute(deqIns[0], deqIns[1]);
		execute(deqIns[0], deqIns[1]);

		for (int i = 0; i < 2; i++) {
			if (!rob.isEmpty()) {
				pcIn = rob.commit(memory);

				// Branch
				if (pcIn != null) {
					rob.clear();
					if (instructionQueue.searchForPc(pcIn)) {
						refillQueue(pcIn);
                        /*
                        while (!instructionQueue.isEmpty() || !found) {
                            branchedInstr = instructionQueue.peek();
                            if (branchedInstr.getPc() == pcIn)
                                found = true;
                            else
                                instructionQueue.dequeue();
                        }
                        */
					} else {
						//rob.clear();
						refillQueue(pcIn);
					}
				}
			}
		}
	}


	private int fetch(Instruction deqIns) {
		int index = rob.add(deqIns);
		reservationStation.add(deqIns, rob, index, deqIns.getPc());
		return index;
	}

	private void execute(Instruction deqIns, Instruction deqIns2) {
		Integer pc = null;
		Integer pc2 = null;
		for (String format : ReservationStation.formats) {
			if (deqIns != null)
				pc = deqIns.getPc();
			if (deqIns2 != null)
				pc2 = deqIns2.getPc();
			reservationStation.remove(format, rob, Main.CC, pc, pc2); //store start cycle in reservationStation
			reservationStation.finish_execution(Main.CC, rob);
		}
	}

	private void loadBufferLogic(Instruction deqIns) {
		try {
			loadBuffer.insertInstruction(deqIns, fetch(deqIns));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void refillQueue(int index) {
		instructionQueue.clear();
		for (int i = index; i < instructionsList.size(); i++) {
			instructionQueue.enqueue(instructionsList.get(i), i);
		}
	}

}
