package com.ariofrio.justhelp;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamRequestBody extends RequestBody {
    private final MediaType mediaType;
    private final InputStream inputStream;

    public InputStreamRequestBody(MediaType mediaType, InputStream inputStream) {
        this.mediaType = mediaType;
        this.inputStream = inputStream;
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public void writeTo(okio.BufferedSink sink) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            sink.write(buffer, 0, read);
        }
    }
}
