package com.tomasolo.sim.Algorithm.MemoryAndBuffer;

public class Memory {
	private final int CAPACITY = 65536;           // 2^16

	private int[] memory;

	public Memory() {
		memory = new int[CAPACITY];
		for (int i = 0; i < CAPACITY; i++) {
			memory[i] = 0;
		}
	}

	public int read(int address) throws Exception {
		if (address >= CAPACITY || address < 0)
			throw new Exception("Memory Address out of range!");
		if ((address % 4) != 0)
			throw new Exception("Address must be a multiple of 4");

		return memory[address];
	}

	public boolean write(int address, int data) throws Exception {
		if (address >= CAPACITY || address < 0)
			throw new Exception("Memory Address out of range!");

		memory[address] = data;
		return true;
	}
}
