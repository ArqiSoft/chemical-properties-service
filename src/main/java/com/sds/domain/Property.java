package com.sds.domain;

public class Property {
    protected String Name;
    protected String Value;
    protected Double Error;

    public Property(String name, String value, Double error) {
        this.Name = name;
        this.Value = value;
        this.Error = error;
    }

    public String getName() {
        return Name;
    }

    public Object getValue() {
        return Value;
    }

    public Double getError() {
        return Error;
    }
}