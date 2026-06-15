package com.inventory.management.inventory;

import org.springframework.stereotype.Component;

@Component
public class InventoryItemMapper {

	public InventoryItem toEntity(InventoryItemRequest request) {
		InventoryItem inventoryItem = new InventoryItem();
		copyToEntity(request, inventoryItem);
		return inventoryItem;
	}

	public void copyToEntity(InventoryItemRequest request, InventoryItem inventoryItem) {
		inventoryItem.setItemName(request.itemName());
		inventoryItem.setDescription(request.description());
		inventoryItem.setCategory(request.category());
		inventoryItem.setQuantity(request.quantity());
		inventoryItem.setThresholdQuantity(request.thresholdQuantity());
		inventoryItem.setSupplier(request.supplier());
		inventoryItem.setPrice(request.price());
	}

	public InventoryItemResponse toResponse(InventoryItem inventoryItem) {
		return new InventoryItemResponse(
			inventoryItem.getId(),
			inventoryItem.getItemName(),
			inventoryItem.getDescription(),
			inventoryItem.getCategory(),
			inventoryItem.getQuantity(),
			inventoryItem.getThresholdQuantity(),
			inventoryItem.getSupplier(),
			inventoryItem.getPrice(),
			inventoryItem.getCreatedAt(),
			inventoryItem.getCreatedBy(),
			inventoryItem.getUpdatedAt(),
			inventoryItem.getUpdatedBy()
		);
	}

}
