package com.avocado.glampe_mobile.model.entity;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceFormat {
    public static String formatUsd(Double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount);
    }
}
