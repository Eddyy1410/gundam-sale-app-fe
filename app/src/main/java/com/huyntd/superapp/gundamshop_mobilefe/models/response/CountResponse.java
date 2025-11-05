package com.huyntd.superapp.gundamshop_mobilefe.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountResponse implements Serializable {

    private Integer count;
    private String type; // Ví dụ: UNREAD_MESSAGE, ORDER_BADGE, CART_BADGE

}
