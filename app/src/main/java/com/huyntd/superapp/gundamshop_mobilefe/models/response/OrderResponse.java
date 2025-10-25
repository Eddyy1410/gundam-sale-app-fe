package com.huyntd.superapp.gundamshop_mobilefe.models.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    int id;
    String billingAddress;
    LocalDateTime orderDate;
    String status;
    String paymentMethod;
    double totalPrice;
    List<OrderItemResponse> orderItems;
}
