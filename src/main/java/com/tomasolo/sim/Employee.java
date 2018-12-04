package com.tomasolo.sim;

import lombok.Data;

@Data
class Employee {

	private String name;
	private String role;

	Employee(String name, String role) {
		this.name = name;
		this.role = role;
	}
}