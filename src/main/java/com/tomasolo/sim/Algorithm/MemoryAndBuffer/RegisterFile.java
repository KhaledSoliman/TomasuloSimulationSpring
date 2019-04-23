package com.tomasolo.sim.Algorithm.MemoryAndBuffer;

public class RegisterFile {
	private static final int CAPACITY = 8;
	private static final int MAX_BITS = 16;

	private static int[] registerFile;

	static {
		registerFile = new int[CAPACITY];
		for (int i = 0; i < CAPACITY; i++)
			registerFile[i] = 0;
	}

	public static int[] getRegisterFile() {
		return registerFile;
	}

	public static boolean write(int index, int data) {
		if (index == 0 || index >= CAPACITY || data > Math.pow(2, MAX_BITS) - 1)
			return false;

		registerFile[index] = data;
		return true;
	}

	public static int read(int index) {
		if (index >= CAPACITY || index < 0)
			System.out.println("Register number not in range!");

		return registerFile[index];
	}

	public static void print() {
		for (int i : registerFile) {
			System.out.println(i);
		}
	}
}
