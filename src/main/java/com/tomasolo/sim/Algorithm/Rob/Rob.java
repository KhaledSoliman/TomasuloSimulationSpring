package com.tomasolo.sim.Algorithm.Rob;

import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.Memory;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegisterFile;

import java.util.*;

public class Rob {
	private LinkedList<RobElement> list;
	private final int CAPACITY = 7;

	public Rob() {
		list = new LinkedList<>();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean isNotFull() {
		return list.size() < CAPACITY;
	}

	private void dupe1(Instruction inst, int dest) {
		RobElement node = new RobElement(inst.getName(), dest, 0,0,false, false);
		list.add(node);
	}

	public int add(Instruction instruction) {
		if (instruction.getName().equals(Instruction.JMP) || instruction.getName().equals(Instruction.BEQ) || instruction.getName().equals(Instruction.RET)) {
			dupe1(instruction, 100);
		} else if (instruction.getName().equals(Instruction.SW)) {
			dupe1(instruction, 101);
		} else {
			dupe1(instruction, instruction.getRegA());
		}
		return list.size() - 1;
	}

	public int findDestination(int register, int index) {
		for (RobElement node : list) {
			if (node.getDestination() == register && node.getIndex() != index) {
				return node.getIndex();
			}
		}
		return -1;
	}

	public boolean isReady(int index) {
		return list.get(index).isReady();
	}

	public Integer getValue(int index) {
		return list.get(index).getValue();
	}

	public boolean setValue(int index, Integer value, Integer jalrValue) {
		RobElement current = list.get(index);
		if (current.getOperation().equals(Instruction.JALR) || current.getOperation().equals(Instruction.SW))
			current.setJalrValue(jalrValue);
		current.setValue(value);
		current.setReady(true);
		return true;
	}


	public Integer commit(Memory memory) {
		Integer PC = null;
		for (RobElement node : list) {
			if (node.isReady()) {
				if (node.getOperation().equals(Instruction.JMP) || node.getOperation().equals(Instruction.RET))
					PC = node.getValue();
				else if (node.getOperation().equals(Instruction.BEQ)) {
					if (node.getValue() != null) {
						PC = node.getValue();
					}
				} else if (node.getOperation().equals(Instruction.JALR)) {
					PC = node.getJalrValue();
					RegisterFile.write(node.getDestination(), node.getValue());
				} else if (node.getOperation().equals(Instruction.SW)) {
					try {
						memory.write(node.getJalrValue(), node.getValue());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else
					RegisterFile.write(node.getDestination(), node.getValue());
			}
		}
		return PC;
	}

	public void clear() {
		list.clear();
	}

	ArrayList<RobElement> asList() {
		return new ArrayList<>(list);
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		for (RobElement item : list) {
			s.append(item.getOperation());
			s.append(item.getDestination());
			s.append(' ');
		}
		return s.toString();
	}
}