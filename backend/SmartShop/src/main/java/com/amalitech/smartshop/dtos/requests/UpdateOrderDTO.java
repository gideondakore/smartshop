package com.amalitech.smartshop.dtos.requests;

import com.amalitech.smartshop.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderDTO {
    private OrderStatus status;
}
