package com.sds.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Severity {
    Fatal(0),
    Error(1),
    Warning(2),
    Information(3);

    private static final Map<Integer, Severity> lookup
            = new HashMap<Integer, Severity>();

    static {
        for (Severity w : EnumSet.allOf(Severity.class))
            lookup.put(w.getCode(), w);
    }

    private int code;

    private Severity(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Severity get(int code) {
        return lookup.get(code);
    }
}
