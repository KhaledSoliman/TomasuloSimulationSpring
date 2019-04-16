package com.tomasolo.sim.Algorithm.Main;

import com.tomasolo.sim.Algorithm.Instruction.Instruction;

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


	public static int getIndexByPc(int pc, ArrayList<Instruction> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getPc() == pc)
				return i;
		}
		return -1;
	}
}
