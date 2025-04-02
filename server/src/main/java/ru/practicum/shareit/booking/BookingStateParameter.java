package ru.practicum.shareit.booking;

public enum BookingStateParameter {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingStateParameter from(String state) {
        for (BookingStateParameter value : BookingStateParameter.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        return null;
    }
}
