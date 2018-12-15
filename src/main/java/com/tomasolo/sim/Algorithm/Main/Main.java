package com.tomasolo.sim.Algorithm.Main;


import com.tomasolo.sim.Algorithm.MemoryAndBuffer.LoadBuffer;
import com.tomasolo.sim.Algorithm.MemoryAndBuffer.RegFile;
import com.tomasolo.sim.InstructionPOJO;
import com.tomasolo.sim.POJO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Timer;
import java.util.TimerTask;

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
		//clkH = new ClkLoadHandler();
		//controller = new Controller();
		//clkInterface = (ClkInterface) controller;
		//updateCCEverySec();
	}


	private static void updateCCEverySec() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (controller.rob.isEmpty() && controller.instrQueue.isEmpty()) {
					timer.cancel();
					System.exit(0);
				} else {
					clkInterface.didUpdate(CC);
					CC++;
					RegFile.print();
				}
			}
		}, 1000, 1000);
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
}
