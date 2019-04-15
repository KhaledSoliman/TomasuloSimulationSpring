package com.tomasolo.sim.Algorithm.Main;

public class ReservationStationGroup {
	private ReservationStationElement[] elements;
	private Integer counter;

	ReservationStationGroup(int numOfElements) {
		elements = new ReservationStationElement[2];
		for (int i = 0; i < numOfElements; i++)
			elements[i] = new ReservationStationElement();
		counter = 0;
	}

	public ReservationStationElement[] getElements() {
		return elements;
	}

	public void setElements(ReservationStationElement[] elements) {
		this.elements = elements;
	}

	public Integer getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public void incrementCounter() {
		this.counter++;
	}
}
