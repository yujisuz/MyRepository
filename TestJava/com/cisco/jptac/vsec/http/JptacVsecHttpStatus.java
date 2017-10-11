package com.cisco.jptac.vsec.http;

public enum JptacVsecHttpStatus {
    OK("200 OK")
    ;

    private final String text;

    private JptacVsecHttpStatus(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
