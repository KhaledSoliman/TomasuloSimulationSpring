package com.tomasolo.sim;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

	EmployeeController() {
	}

	@PostMapping("/employees")
	void newEmployee(@RequestBody Employee newEmployee) {
	}

}
