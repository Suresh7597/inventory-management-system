package com.inventory.management.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Item name is required")
	@Size(max = 150, message = "Item name must be 150 characters or fewer")
	@Column(name = "item_name", nullable = false, length = 150)
	private String itemName;

	@Size(max = 500, message = "Description must be 500 characters or fewer")
	@Column(length = 500)
	private String description;

	@NotBlank(message = "Category is required")
	@Size(max = 100, message = "Category must be 100 characters or fewer")
	@Column(nullable = false, length = 100)
	private String category;

	@NotNull(message = "Quantity is required")
	@Min(value = 0, message = "Quantity cannot be negative")
	@Column(nullable = false)
	private Integer quantity;

	@NotNull(message = "Threshold quantity is required")
	@Min(value = 0, message = "Threshold quantity cannot be negative")
	@Column(name = "threshold_quantity", nullable = false)
	private Integer thresholdQuantity;

	@NotBlank(message = "Supplier is required")
	@Size(max = 150, message = "Supplier must be 150 characters or fewer")
	@Column(nullable = false, length = 150)
	private String supplier;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than 0")
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal price;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "created_by", length = 150, updatable = false)
	private String createdBy;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "updated_by", length = 150)
	private String updatedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getThresholdQuantity() {
		return thresholdQuantity;
	}

	public void setThresholdQuantity(Integer thresholdQuantity) {
		this.thresholdQuantity = thresholdQuantity;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

}
