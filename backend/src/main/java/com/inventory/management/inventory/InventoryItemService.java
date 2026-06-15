package com.inventory.management.inventory;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryItemService {

	private final InventoryItemRepository inventoryItemRepository;
	private final InventoryItemMapper inventoryItemMapper;

	public InventoryItemService(
		InventoryItemRepository inventoryItemRepository,
		InventoryItemMapper inventoryItemMapper
	) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.inventoryItemMapper = inventoryItemMapper;
	}

	@Transactional(readOnly = true)
	public List<InventoryItem> findAll() {
		return inventoryItemRepository.findAll(Sort.by(Sort.Direction.ASC, "itemName"));
	}

	@Transactional(readOnly = true)
	public InventoryItem findById(Long id) {
		return inventoryItemRepository.findById(id)
			.orElseThrow(() -> new InventoryItemNotFoundException(id));
	}

	public InventoryItem create(InventoryItemRequest request) {
		InventoryItem inventoryItem = inventoryItemMapper.toEntity(request);
		String currentUser = currentUsername();
		inventoryItem.setCreatedBy(currentUser);
		inventoryItem.setUpdatedBy(currentUser);
		return inventoryItemRepository.save(inventoryItem);
	}

	public InventoryItem update(Long id, InventoryItemRequest request) {
		InventoryItem existingItem = findById(id);
		inventoryItemMapper.copyToEntity(request, existingItem);
		existingItem.setUpdatedBy(currentUsername());
		return inventoryItemRepository.save(existingItem);
	}

	public void delete(Long id) {
		if (!inventoryItemRepository.existsById(id)) {
			throw new InventoryItemNotFoundException(id);
		}
		inventoryItemRepository.deleteById(id);
	}

	private String currentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getName() == null) {
			return "system";
		}
		return authentication.getName();
	}

}
