package com.inventory.management.inventory;

public class InventoryItemNotFoundException extends RuntimeException {

	public InventoryItemNotFoundException(Long id) {
		super("Inventory item " + id + " was not found");
	}

}
