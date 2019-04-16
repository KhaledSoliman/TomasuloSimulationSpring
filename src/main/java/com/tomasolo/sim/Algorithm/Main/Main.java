package com.tomasolo.sim.Algorithm.Main;


import com.tomasolo.sim.Algorithm.Instruction.Instruction;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.LoadBuffer;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegFile;
import com.tomasolo.sim.InstructionPOJO;
import com.tomasolo.sim.POJO;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Consumer;

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
		clkInterface = controller;
		updateCCEverySec();

		Thread.sleep(4000);
		return response;
	}


	private static void updateCCEverySec() {
		Timer timer = new Timer();
		response.clear();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Main.response.add(buildRes());
				if (controller.rob.isEmpty() && controller.instructionQueue.isEmpty()) {
					timer.cancel();
					double ipc = (float) controller.reservationStation.getNumExecutedInstructions() / CC;
					if (controller.reservationStation.getNumBranchInstrs() != 0) {
						float missPredictionRate = (float) (controller.missPredictionCount / controller.reservationStation.getNumBranchInstrs()) * 100;
						System.out.println("IPC = " + ipc + " \nMiss Prediction Rate = " + missPredictionRate);
					} else {
						System.out.println("IPC = " + ipc + " \nMiss Prediction Rate = No Branch Instructions were executed");
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
		ArrayList<RobNode> robResponse = controller.rob.asList();
		ArrayList<String[]> rsResponse = new ArrayList<>();
		return new Response(robResponse, rsResponse, RegFile.getRf());
	}

	private static ArrayList<Instruction> convertToInstructions(InstructionPOJO[] pojos) {
		ArrayList<Instruction> list = new ArrayList<>();
		for (InstructionPOJO instructionPOJO : pojos) {
			try {
				Instruction instruction = new Instruction(instructionPOJO.getName(), instructionPOJO.getOperandsAsIntegers());
				list.add(instruction);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
