package com.inventory.management.inventory;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory-items")
public class InventoryItemController {

	private final InventoryItemService inventoryItemService;
	private final InventoryItemMapper inventoryItemMapper;

	public InventoryItemController(
		InventoryItemService inventoryItemService,
		InventoryItemMapper inventoryItemMapper
	) {
		this.inventoryItemService = inventoryItemService;
		this.inventoryItemMapper = inventoryItemMapper;
	}

	@GetMapping
	public List<InventoryItemResponse> list() {
		return inventoryItemService.findAll().stream()
			.map(inventoryItemMapper::toResponse)
			.toList();
	}

	@GetMapping("/{id}")
	public InventoryItemResponse get(@PathVariable Long id) {
		return inventoryItemMapper.toResponse(inventoryItemService.findById(id));
	}

	@PostMapping
	public ResponseEntity<InventoryItemResponse> create(@Valid @RequestBody InventoryItemRequest request) {
		InventoryItem createdItem = inventoryItemService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(createdItem.getId())
			.toUri();
		return ResponseEntity.created(location).body(inventoryItemMapper.toResponse(createdItem));
	}

	@PutMapping("/{id}")
	public InventoryItemResponse update(@PathVariable Long id, @Valid @RequestBody InventoryItemRequest request) {
		return inventoryItemMapper.toResponse(inventoryItemService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		inventoryItemService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
