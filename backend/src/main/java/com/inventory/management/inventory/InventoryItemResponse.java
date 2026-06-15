package com.inventory.management.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryItemResponse(
	Long id,
	String itemName,
	String description,
	String category,
	Integer quantity,
	Integer thresholdQuantity,
	String supplier,
	BigDecimal price,
	LocalDateTime createdAt,
	String createdBy,
	LocalDateTime updatedAt,
	String updatedBy
) {
}
