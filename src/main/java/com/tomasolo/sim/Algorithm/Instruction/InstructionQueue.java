package com.tomasolo.sim.Algorithm.Instruction;

import com.tomasolo.sim.Algorithm.Main.Controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class InstructionQueue {
	private Queue<Instruction> instructionQueue;
	private int size;
	private int pc;
	private final int MAX_CAPACITY = 4;

	/**
	 * Initialize queue with size 0 and empty LL
	 */
	public InstructionQueue() {
		size = 0;
		pc = 0;
		instructionQueue = new LinkedList<>();
	}

	/**
	 * Enqueue an instruction
	 *
	 * @param instruction instruction to be added to queue
	 * @return bool true if enqueued false if max capacity
	 */
	public boolean enqueue(Instruction instruction) {
		if (size > MAX_CAPACITY)
			return false;
		else {
			instruction.setPc(++pc);
			instructionQueue.add(instruction);
			size++;
			return true;
		}
	}

	public boolean enqueue(Instruction instruction, int pc) {
		if (size > MAX_CAPACITY)
			return false;
		else {
			instruction.setPc(pc);
			instructionQueue.add(instruction);
			size++;
			return true;
		}
	}

	/**
	 * Dequeue an instruction
	 *
	 * @param list
	 * @param awaitingIndex
	 * @return Instruction object
	 */
	public Instruction dequeue(ArrayList<Instruction> list, int awaitingIndex) {
		size--;
		if (size < MAX_CAPACITY && Controller.awaitingInstructionIndex < list.size()) {
			instructionQueue.add(list.get(awaitingIndex));
			Controller.awaitingInstructionIndex++;
		}
		return instructionQueue.remove();
	}

	/**
	 * clear instruction queue
	 */
	public void clear() {
		while (!instructionQueue.isEmpty()) {
			instructionQueue.remove();
		}
	}

	public Instruction peek() {
		return instructionQueue.peek();
	}

	public boolean searchForPc(int pc) {
		for (Instruction instruction : instructionQueue) {
			if (instruction.getPc() == pc)
				return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return instructionQueue.isEmpty();
	}

	public int getSize() {
		return size;
	}
}
