package net.ollie.protobuf;

import com.google.protobuf.Timestamp;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;

public interface WritesProto {

    void writeTo(OutputStream out) throws IOException;

    static Timestamp writeTimestamp(final Instant time) {
        return Timestamp.newBuilder()
                .setSeconds(time.getEpochSecond())
                .setNanos(time.getNano())
                .build();
    }

}
