package com.tomasolo.sim.Algorithm.Instruction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Instruction {
	private String name, format;
	private int regA, regB, regC;
	private int immediate;
	private int pc;

	// All Formats
	public static final String
			FORMAT_LW_SW = "LOAD/STORE",
			FORMAT_UNCONDITIONAL_BRANCH = "UNCONDITIONAL BRANCH",
			FORMAT_CONDITIONAL_BRANCH = "CONDITIONAL BRANCH",
			FORMAT_JALR = "JALR",
			FORMAT_RET = "RET",
			FORMAT_ARITHMETIC = "ARITHMETIC INSTRS",
			FORMAT_ARITHMETIC_IMM = "ARITHMETIC Imm INSTRS";
	// All Instructions
	public static final String
			LW = "LW",
			SW = "SW",
			JMP = "JMP",
			BEQ = "BEQ",
			JALR = "JALR",
			RET = "RET",
			ADD = "ADD",
			SUB = "SUB",
			ADDI = "ADDI",
			NAND = "NAND",
			MUL = "MUL";

	@JsonCreator
	public Instruction(@JsonProperty(value = "name") String name, @JsonProperty(value = "operands") String[] operandsAsString) throws Exception {
		this.name = name;
		Integer[] operands = this.getOperandsAsIntegers(operandsAsString);
		switch (name) {
			case LW:
			case SW:
				this.format = FORMAT_LW_SW;
				this.regA = operands[0];
				this.regB = operands[1];
				this.immediate = operands[2];
				break;
			case ADD:
			case SUB:
			case MUL:
			case NAND:
				this.format = FORMAT_ARITHMETIC;
				this.regA = operands[0];
				this.regB = operands[1];
				this.regC = operands[2];
				break;
			case ADDI:
				this.format = FORMAT_ARITHMETIC_IMM;
				this.regA = operands[0];
				this.regB = operands[1];
				this.immediate = operands[2];
				break;
			case BEQ:
				this.format = FORMAT_CONDITIONAL_BRANCH;
				this.regA = operands[0];
				this.regB = operands[1];
				this.immediate = operands[2];
				break;
			case JALR:
				this.name = name;
				this.format = FORMAT_JALR;
				this.regA = operands[0];
				this.regB = operands[1];
				break;
			case JMP:
				this.format = FORMAT_UNCONDITIONAL_BRANCH;
				this.immediate = operands[0];
				break;
			case RET:
				this.format = FORMAT_RET;
				this.regA = operands[0];
				break;
			default:
				throw new Exception("Exception: Please check your format");
		}
	}

	public String getName() {
		return name;
	}

	public String getFormat() {
		return format;
	}

	public int getRegA() {
		return regA;
	}

	public int getRegB() {
		return regB;
	}

	public int getRegC() {
		return regC;
	}

	public int getImmediate() {
		return immediate;
	}

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	private Integer[] getOperandsAsIntegers(String[] operands) {
		Integer[] operandsAsIntegerArray = new Integer[operands.length];
		for (int i = 0; i < operands.length; i++) {
			operandsAsIntegerArray[i] = Integer.parseInt(operands[i]);
		}
		return operandsAsIntegerArray;
	}
}
