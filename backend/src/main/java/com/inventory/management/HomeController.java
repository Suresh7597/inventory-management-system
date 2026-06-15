package com.inventory.management;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public Map<String, String> home() {
		return Map.of(
			"application", "Inventory Management System",
			"milestone", "Milestone 1 Completion",
			"inventoryApi", "/api/inventory-items"
		);
	}

}
