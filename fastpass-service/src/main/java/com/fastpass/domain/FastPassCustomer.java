package com.fastpass.domain;

public record FastPassCustomer(
        String fastPassId,
        String customerFullName,
        String customerPhone,
        Float currentBalance
) { }
