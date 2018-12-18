package com.tomasolo.sim.Algorithm.Main;


import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.LoadBuffer;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegFile;
import com.tomasolo.sim.InstructionPOJO;
import com.tomasolo.sim.POJO;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@RestController
public class Main {
	public static int CC = 1;
	private static ClkInterface clkInterface;
	private static Controller controller;
	public static ClkLoadHandler clkH;
	private static ArrayList<Response> response = new ArrayList<>();

	@PostMapping("/")
	@CrossOrigin(origins = "http://localhost:3000")
	@ResponseBody
	public static ArrayList<Response> main(@RequestBody POJO args) throws InterruptedException {
		clkH = new ClkLoadHandler();
		controller = new Controller(convertToInstructions(args.getInstructions()));
		clkInterface = (ClkInterface) controller;
		updateCCEverySec();

		 Thread.sleep(2000);
		 return response;
	}


	private static void updateCCEverySec() {
		Timer timer = new Timer();
		response.clear();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Main.response.add(buildRes());
				if (controller.rob.isEmpty() && controller.instrQueue.isEmpty()) {
					timer.cancel();
					double ipc = (float) controller.rs.getNumExecutedInstructions() / CC;
					if(controller.rs.getNumBranchInstrs() != 0) {
						float mispredictionRate = (float) (controller.mispredictionNum / controller.rs.getNumBranchInstrs()) * 100;
						System.out.println("IPC = " + ipc + " \nMisprediction Rate = " + mispredictionRate);
					}
					else {
						System.out.println("IPC = " + ipc + " \nMisprediction Rate = No Branch Instructions were executed");
					}
				} else {
					clkInterface.didUpdate(CC);
					CC++;
					RegFile.print();
				}
			}
		}, 10, 100);
	}


	interface ClkInterface {
		void didUpdate(int CC);
	}

	static class ClkLoadHandler implements LoadBuffer.NextCCInterface {
		@Override
		public void nextCycle(boolean loadEx) {
			Main.CC++;
			clkInterface.didUpdate(Main.CC);
		}
	}

	private static Response buildRes() {

		ArrayList<ROB_NODE> robResponse = controller.rob.asList();
		/*
			Iterator iteratorROB = controller.rob.iterator();
			iteratorROB.forEachRemaining(new Consumer<ROB_NODE>() {
				@Override
				public void accept(Object o) {
					robResponse.add(o.toString());
				}
			});
		*/
		ArrayList<String[]> rsResponse = new ArrayList<>();
		Iterator<Reservation_Station_Element> iteratorRS = controller.rs.iterator();
		iteratorRS.forEachRemaining(new Consumer<Reservation_Station_Element>() {
			@Override
			public void accept(Reservation_Station_Element reservation_station_element) {
				rsResponse.add(reservation_station_element.toArray());
			}
		});

		return new Response(robResponse, rsResponse);
	}

	private static ArrayList<Instruction> convertToInstructions(InstructionPOJO[] pojos) {
		ArrayList<Instruction> list = new ArrayList<>();
		for (InstructionPOJO i : pojos) {
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
			} else if (i.getName().equals(Instruction.ADDI)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_ARITHMETIC_IMM,
							Integer.parseInt(i.getOperands()[0]),
							Integer.parseInt(i.getOperands()[1]),
							Integer.parseInt(i.getOperands()[2]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (i.getName().equals(Instruction.BEQ)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_CONDITIONAL_BRANCH,
							Integer.parseInt(i.getOperands()[0]),
							Integer.parseInt(i.getOperands()[1]),
							Integer.parseInt(i.getOperands()[2]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (i.getName().equals(Instruction.JALR)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_JALR,
							Integer.parseInt(i.getOperands()[0]),
							Integer.parseInt(i.getOperands()[1]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (i.getName().equals(Instruction.JMP)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_UNCONDITIONAL_BRANCH,
							Integer.parseInt(i.getOperands()[0]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (i.getName().equals(Instruction.RET)) {
				try {
					Instruction instruction = new Instruction(i.getName(), Instruction.FORMAT_RET,
							Integer.parseInt(i.getOperands()[0]));
					list.add(instruction);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (i.getName().equals(Instruction.LW) || i.getName().equals(Instruction.SW)) {
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
