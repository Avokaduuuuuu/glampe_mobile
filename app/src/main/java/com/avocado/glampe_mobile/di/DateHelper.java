package com.avocado.glampe_mobile.di;

import android.os.Build;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateHelper {
    public static Long localDateToMillis(LocalDate localDate) {
        if (localDate == null) return null;
        return localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate millisToLocalDate(Long millis) {
        if (millis == null) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return LocalDate.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        }
        return null;
    }
}
