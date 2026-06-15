package com.inventory.management.inventory;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InventoryItemRequest(
	@NotBlank(message = "Item name is required")
	@Size(max = 150, message = "Item name must be 150 characters or fewer")
	String itemName,

	@Size(max = 500, message = "Description must be 500 characters or fewer")
	String description,

	@NotBlank(message = "Category is required")
	@Size(max = 100, message = "Category must be 100 characters or fewer")
	String category,

	@NotNull(message = "Quantity is required")
	@Min(value = 0, message = "Quantity cannot be negative")
	Integer quantity,

	@NotNull(message = "Threshold quantity is required")
	@Min(value = 0, message = "Threshold quantity cannot be negative")
	Integer thresholdQuantity,

	@NotBlank(message = "Supplier is required")
	@Size(max = 150, message = "Supplier must be 150 characters or fewer")
	String supplier,

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than 0")
	BigDecimal price
) {
}
