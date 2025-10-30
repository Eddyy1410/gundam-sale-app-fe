package com.huyntd.superapp.gundamshop_mobilefe.models.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private int userId;
    private double totalPrice;
    private String paymentMethod;
    private String billingAddress;
    private List<OrderItemRequest> orderItems;
}
