package com.tomasolo.sim.Algorithm.MemoryAndBuffer;

import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.Main.Main;
import com.tomasolo.sim.Algorithm.Main.Controller;


public class LoadBuffer {
	private final int LOAD_CAPACITY = 2;
	private final int STORE_CAPACITY = 2;
	private final int MAX_CAPACITY = LOAD_CAPACITY + STORE_CAPACITY;

	private int load_size;
	private int store_size;
	private int[] buffer;
	private MemoryInterface memInterface;
	private NextCCInterface clkInterface;

	public LoadBuffer(Controller controller) {
		buffer = new int[MAX_CAPACITY];
		for (int i = 0; i < MAX_CAPACITY; i++) {
			buffer[i] = 0;
		}
		load_size = 0;
		store_size = 0;
		memInterface = controller;
		clkInterface = Main.clkH;
	}


	public boolean insertInstruction(Instruction instruction, int robIndex) throws Exception {
		if (!freeSlot(instruction)) {
			return false;
		} else {
			int index;
			if (instruction.getName().equals(Instruction.LW)) {
				index = getFreeLoadSlot(load_size);
				load_size++;
			} else {
				index = getFreeStoreSlot(store_size);
				store_size++;
			}

			// Computing Address for Load or Store

			// CC 1: Issuing
			// Starting Computing Address:
			// A = Imm
			buffer[index] = instruction.getImmediate();
			System.out.println("CC: " + Main.CC + " Load Buffer: entry #" + index + ", Address: " + buffer[index]);

			// CC 2: A = Imm + Regs[Rs2]
			nextCycle(Main.CC);
			System.out.println("Expected : 2, Found: " + Main.CC);
			try {
				buffer[index] += RegisterFile.read(instruction.getRegB());
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Cannot read from Register File");
			}

			System.out.println("CC: " + Main.CC + " Load Buffer: entry #" + index + ", Address: " + buffer[index]);


			// CC 3: Writing
			nextCycle(Main.CC);
			// Case Load
			if (instruction.getName().equals(Instruction.LW)) {
				// Free the slot
				load_size--;
				// Load from Memory
				int loadedValue = memInterface.load(buffer[index]);
				// TODO:: Change the -1 to be another error value
				if (loadedValue == -1)
					return false;
					// Callback once done
				else {
					return memInterface.memoryLoadDone(robIndex, loadedValue);
				}
			}
			// Case Store
			else {
				// Free the slot
				store_size--;
				// Store in Memory
				return memInterface.store(robIndex, buffer[index], instruction.getRegA());
			}
		}
	}

	private int getFreeStoreSlot(int storeSize) {
		return storeSize + 2;
	}

	private int getFreeLoadSlot(int loadSize) {
		return loadSize;
	}

	private boolean freeSlot(Instruction instruction) {
		return (instruction.getName().equals(Instruction.LW) && load_size < LOAD_CAPACITY) ||
				(instruction.getName().equals(Instruction.SW) && store_size < STORE_CAPACITY);
	}

	public boolean loadIsFree() {
		return load_size < LOAD_CAPACITY;
	}

	public boolean storeIsFree() {
		return load_size < LOAD_CAPACITY;
	}

	private void nextCycle(int cc) {
		boolean flag = true;
		while (flag) {
			System.out.print("");
			clkInterface.nextCycle(true);
			if (cc < Main.CC) {
				flag = false;
			}
		}
	}

	public interface MemoryInterface {
		int load(int address);

		boolean store(int rob_index, int address, int data);

		boolean memoryLoadDone(int rob_index, int data);
	}

	public interface NextCCInterface {
		void nextCycle(boolean loadEx);
	}
}
