package com.dell.prototype.apm.instrumentation;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ChrResponseWrapper extends HttpServletResponseWrapper {

    CharArrayWriter output;
    public HttpServletResponse response;

    public ChrResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
        this.output = new CharArrayWriter();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                output.write(b);
            }
        };
    }

    public PrintWriter getSuperWriter() {
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    public String toString() {
        return output.toString();
    }
}
