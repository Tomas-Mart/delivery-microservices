package org.example.dto;

import lombok.Data;
import org.example.model.Order;
import org.example.enums.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private String courierId;
    private String zoneId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderDto fromEntity(Order order) {
        if (order == null) return null;

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCourierId(order.getCourierId());
        dto.setZoneId(order.getZoneId());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }
}