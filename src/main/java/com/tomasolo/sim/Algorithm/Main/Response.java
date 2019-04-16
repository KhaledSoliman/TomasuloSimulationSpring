package com.tomasolo.sim.Algorithm.Main;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Response {
	ArrayList<RobNode> robResponse;
	ArrayList<String[]> rsResponses;
	int[] rf;

	public Response(ArrayList<RobNode> robResponse, ArrayList<String[]> rsResponses, int[] rf) {
		this.robResponse = robResponse;
		this.rsResponses = rsResponses;
		this.rf = rf;
	}

	public int[] getRf() {
		return rf;
	}

	public void setRf(int[] rf) {
		this.rf = rf;
	}

	public ArrayList<RobNode> getRobResponse() {
		return robResponse;
	}

	public void setRobResponse(ArrayList<RobNode> robResponse) {
		this.robResponse = robResponse;
	}

	public ArrayList<String[]> getRsResponses() {
		return rsResponses;
	}

	public void setRsResponses(ArrayList<String[]> rsResponses) {
		this.rsResponses = rsResponses;
	}
}
