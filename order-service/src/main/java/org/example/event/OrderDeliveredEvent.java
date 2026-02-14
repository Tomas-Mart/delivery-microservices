package org.example.event;

import lombok.Data;

@Data
public class OrderDeliveredEvent {
    private Long orderId;
    private String courierId;

    public OrderDeliveredEvent(Long orderId, String courierId) {
        this.orderId = orderId;
        this.courierId = courierId;
    }
}