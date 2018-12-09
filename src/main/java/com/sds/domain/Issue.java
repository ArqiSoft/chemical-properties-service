package com.sds.domain;

public class Issue {
    protected String Code;
    protected Severity Severity;
    protected String Title;
    protected String Message;
    protected String AuxInfo;

    public Issue(String code, com.sds.domain.Severity severity, String title, String message, String auxInfo) {
        Code = code;
        Severity = severity;
        Title = title;
        Message = message;
        AuxInfo = auxInfo;
    }

    public String getCode() {
        return Code;
    }

    public com.sds.domain.Severity getSeverity() {
        return Severity;
    }

    public String getTitle() {
        return Title;
    }

    public String getMessage() {
        return Message;
    }

    public String getAuxInfo() {
        return AuxInfo;
    }
}
