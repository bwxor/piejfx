package com.bwxor.piejfx.type;

public enum FilterMode {
    ALL("All"), INSTALLED("Installed"), NOT_INSTALLED("Not Installed");

    private final String buttonValue;

    FilterMode(String buttonValue) {
        this.buttonValue = buttonValue;
    }

    public static FilterMode parse(String input) {
        if (input == null) {
            return null;
        }

        for (FilterMode f: FilterMode.values()) {
            if (f.buttonValue.equals(input)) {
                return f;
            }
        }

        return null;
    }
}
