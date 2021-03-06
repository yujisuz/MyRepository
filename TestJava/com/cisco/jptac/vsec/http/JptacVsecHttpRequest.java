package com.cisco.jptac.vsec.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

public class JptacVsecHttpRequest {

    public static final String CRLF = "\r\n";

    private final String headerText;
    private final String bodyText;

    public JptacVsecHttpRequest(InputStream input) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            ) {

            this.headerText = this.readHeader(in);
            this.bodyText = this.readBody(in);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readHeader(BufferedReader in) throws IOException {
        String line = in.readLine();
        StringBuilder header = new StringBuilder();

        while (line != null && !line.isEmpty()) {
            header.append(line + CRLF);
            line = in.readLine();
        }

        return header.toString();
    }

    private String readBody(BufferedReader in) throws IOException {
        if (this.isChunkedTransfer()) { // ★チャンク形式かどうか判断
            return this.readBodyByChunkedTransfer(in);
        } else {
            return this.readBodyByContentLength(in);
        }
    }

    private boolean isChunkedTransfer() {
        return Stream.of(this.headerText.split(CRLF))
                     .filter(headerLine -> headerLine.startsWith("Transfer-Encoding"))
                     .map(transferEncoding -> transferEncoding.split(":")[1].trim())
                     .anyMatch(s -> "chunked".equals(s)); // ★Transfer-Encoding: chunked がヘッダーにあればチャンク形式
    }

    // ★ここでチャンク形式のボディを読み取り
    private String readBodyByChunkedTransfer(BufferedReader in) throws IOException {
        StringBuilder body = new StringBuilder();

        int chunkSize = Integer.parseInt(in.readLine(), 16);

        while (chunkSize != 0) {
            char[] buffer = new char[chunkSize];
            in.read(buffer);

            body.append(buffer);

            in.readLine(); // chunk-body の末尾にある CRLF を読み飛ばす
            chunkSize = Integer.parseInt(in.readLine(), 16);
        }

        return body.toString();
    }

    private String readBodyByContentLength(BufferedReader in) throws IOException {
        final int contentLength = this.getContentLength();

        if (contentLength <= 0) {
            return null;
        }

        char[] c = new char[contentLength];
        in.read(c);

        return new String(c);
    }

    private int getContentLength() {
        return Stream.of(this.headerText.split(CRLF))
                     .filter(headerLine -> headerLine.startsWith("Content-Length"))
                     .map(contentLengthHeader -> contentLengthHeader.split(":")[1].trim())
                     .mapToInt(Integer::parseInt)
                     .findFirst().orElse(0);
    }

    public String getHeaderText() {
        return this.headerText;
    }

    public String getBodyText() {
        return this.bodyText;
    }

}
