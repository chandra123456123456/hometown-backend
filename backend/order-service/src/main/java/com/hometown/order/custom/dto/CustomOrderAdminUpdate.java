package com.hometown.order.custom.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomOrderAdminUpdate(
        @NotNull Long id,
        String description,
        String status,
        BigDecimal quotedPrice,
        LocalDate deliveryDate,
        String adminNotes
) {}
