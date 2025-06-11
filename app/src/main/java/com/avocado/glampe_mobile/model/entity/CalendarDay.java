package com.avocado.glampe_mobile.model.entity;

import java.time.LocalDate;

public class CalendarDay {
    private LocalDate date;
    private boolean isCurrentMonth;
    private boolean isSelected;
    private boolean isInRange;
    private boolean isToday;
    private boolean isPast;

    public CalendarDay(LocalDate date, boolean isCurrentMonth, boolean isSelected,
                       boolean isInRange, boolean isToday, boolean isPast) {
        this.date = date;
        this.isCurrentMonth = isCurrentMonth;
        this.isSelected = isSelected;
        this.isInRange = isInRange;
        this.isToday = isToday;
        this.isPast = isPast;
    }

    // Getters
    public LocalDate getDate() { return date; }
    public boolean isCurrentMonth() { return isCurrentMonth; }
    public boolean isSelected() { return isSelected; }
    public boolean isInRange() { return isInRange; }
    public boolean isToday() { return isToday; }
    public boolean isPast() { return isPast; }

    // Helper method to check if day is selectable
    public boolean isSelectable() {
        return isCurrentMonth && !isPast;
    }

    // Setters
    public void setSelected(boolean selected) { isSelected = selected; }
    public void setInRange(boolean inRange) { isInRange = inRange; }
}
