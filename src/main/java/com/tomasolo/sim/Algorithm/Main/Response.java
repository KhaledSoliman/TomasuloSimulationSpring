package com.tomasolo.sim.Algorithm.Main;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Response {
	ArrayList<ROB_NODE> robResponse;
	ArrayList<String[]> rsResponses;
	int[] rf;

	public Response(ArrayList<ROB_NODE> robResponse, ArrayList<String[]> rsResponses, int[] rf) {
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

	public ArrayList<ROB_NODE> getRobResponse() {
		return robResponse;
	}

	public void setRobResponse(ArrayList<ROB_NODE> robResponse) {
		this.robResponse = robResponse;
	}

	public ArrayList<String[]> getRsResponses() {
		return rsResponses;
	}

	public void setRsResponses(ArrayList<String[]> rsResponses) {
		this.rsResponses = rsResponses;
	}
}
