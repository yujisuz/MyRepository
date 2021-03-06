package com.cisco.jptac.vsec.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JptacVsecHttpResponse {

    private final JptacVsecHttpStatus status;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpResponse(JptacVsecHttpStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    public void addHeader(String string, Object value) {
        this.headers.put(string, value.toString());
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void writeTo(OutputStream out) throws IOException {
        IOUtil.println(out, "HTTP/1.1 " + this.status);

        this.headers.forEach((key, value) -> {
            IOUtil.println(out, key + ": " + value);
        });

        if (this.body != null) {
            IOUtil.println(out, "");
            IOUtil.print(out, this.body);
        }
    }
}
