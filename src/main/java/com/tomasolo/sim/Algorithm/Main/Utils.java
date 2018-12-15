package com.tomasolo.sim.Algorithm.Main;

import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.Instruction.InstructionQueue;

import java.util.ArrayList;

public class Utils {
	public static ArrayList<Instruction> fillArray(ArrayList<Instruction> instrs) {
		ArrayList<Instruction> list = new ArrayList<>();
		for (int i = 0; i < instrs.size(); i++) {
			Instruction instr = instrs.get(i);
			instr.setPc(i);
			list.add(instr);
		}

		return list;
	}

	public static void DeQueueAll(InstructionQueue instrQueue, boolean consoleLog) {
		if (consoleLog) {
			for (int i = 0; i < instrQueue.getSize(); i++) {
				System.out.println(instrQueue.dequeue());
			}
		} else {
			for (int i = 0; i < instrQueue.getSize(); i++) {
				instrQueue.dequeue();
			}
		}
	}

	public static int getIndexByPc(int pc, ArrayList<Instruction> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getPc() == pc)
				return i;
		}
		return -1;
	}
}
