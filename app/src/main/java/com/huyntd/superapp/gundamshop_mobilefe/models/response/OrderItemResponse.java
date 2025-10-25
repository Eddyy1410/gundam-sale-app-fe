package com.huyntd.superapp.gundamshop_mobilefe.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    int id;
    int orderId;
    int productId;
    String productImage;
    String productName;
    int quantity;
}