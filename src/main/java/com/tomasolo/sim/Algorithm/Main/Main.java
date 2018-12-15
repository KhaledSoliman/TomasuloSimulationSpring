package com.tomasolo.sim.Algorithm.Main;


import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.LoadBuffer;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegFile;
import com.tomasolo.sim.InstructionPOJO;
import com.tomasolo.sim.POJO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@RestController
public class Main {
	public static int CC = 1;
	private static ClkInterface clkInterface;
	private static Controller controller;
	private static boolean loadEx;
	public static ClkLoadHandler clkH;

	@PostMapping("/")
	@CrossOrigin(origins = "http://localhost:3000")
	public static void main(@RequestBody POJO args) {
		for (InstructionPOJO instr : args.getInstructions()) {
			System.out.print(instr.getName());
		}
		clkH = new ClkLoadHandler();
		controller = new Controller(convertToInstructions(args.getInstructions()));
		clkInterface = (ClkInterface) controller;
		updateCCEverySec();
	}


	private static void updateCCEverySec() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				buildRes();
				if (controller.rob.isEmpty() && controller.instrQueue.isEmpty()) {
					timer.cancel();
					System.exit(0);
				} else {
					clkInterface.didUpdate(CC);
					CC++;
					RegFile.print();
				}
			}
		}, 10, 10);
	}


	interface ClkInterface {
		void didUpdate(int CC);
	}

	static class ClkLoadHandler implements LoadBuffer.NextCCInterface {
		@Override
		public void nextCycle(boolean loadEx) {
			Main.CC++;
			Main.loadEx = loadEx;
			clkInterface.didUpdate(Main.CC);
		}
	}

	private static String[] buildRes() {

		ArrayList<String> robResponse = new ArrayList<>();
		Iterator iteratorROB = controller.rob.iterator();
		iteratorROB.forEachRemaining(new Consumer() {
			@Override
			public void accept(Object o) {
				robResponse.add(o.toString());
			}
		});

		ArrayList<String[]> rsResponse = new ArrayList<>();
		Iterator<Reservation_Station_Element> iteratorRS = controller.rs.iterator();
		iteratorRS.forEachRemaining(new Consumer<Reservation_Station_Element>() {
			@Override
			public void accept(Reservation_Station_Element reservation_station_element) {
				rsResponse.add(reservation_station_element.toArray());
			}
		});
		String[] response = new String[rsResponse.size() + robResponse.size()];
		System.arraycopy(rsResponse, 0, response, 0, rsResponse.size());
		System.arraycopy(robResponse, 0, response, rsResponse.size(), robResponse.size());

		return response;
	}

	private static ArrayList<Instruction> convertToInstructions(InstructionPOJO[] pojos) {
		ArrayList<Instruction> list = new ArrayList<>();
		for (InstructionPOJO i: pojos) {
			if (i.getName().equals(Instruction.ADD) || i.getName().equals(Instruction.SUB)
					|| i.getName().equals(Instruction.MUL) || i.getName().equals(Instruction.NAND)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_ARITHMETIC,
							Integer.parseInt(i.getOperands()[0]),
                            Integer.parseInt(i.getOperands()[1]),
                            Integer.parseInt(i.getOperands()[2]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(i.getName().equals(Instruction.ADDI)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_ARITHMETIC_IMM,
							Integer.parseInt(i.getOperands()[0]),
							Integer.parseInt(i.getOperands()[1]),
							Integer.parseInt(i.getOperands()[2]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (i.getName().equals(Instruction.BEQ)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_CONDITIONAL_BRANCH,
							Integer.parseInt(i.getOperands()[0]),
							Integer.parseInt(i.getOperands()[1]),
							Integer.parseInt(i.getOperands()[2]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (i.getName().equals(Instruction.JALR)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_JALR,
							Integer.parseInt(i.getOperands()[0]),
							Integer.parseInt(i.getOperands()[1]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (i.getName().equals(Instruction.JMP)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_UNCONDITIONAL_BRANCH,
							Integer.parseInt(i.getOperands()[0]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (i.getName().equals(Instruction.RET)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_RET,
							Integer.parseInt(i.getOperands()[0]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (i.getName().equals(Instruction.LW) || i.getName().equals(Instruction.SW)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_LW_SW,
							Integer.parseInt(i.getOperands()[0]),
							Integer.parseInt(i.getOperands()[1]),
							Integer.parseInt(i.getOperands()[2]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

}
