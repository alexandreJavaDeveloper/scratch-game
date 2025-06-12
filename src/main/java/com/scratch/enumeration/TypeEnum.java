package com.scratch.enumeration;

public enum TypeEnum {
    standard("standard"),
    bonus("bonus");

    private final String value;

    TypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}