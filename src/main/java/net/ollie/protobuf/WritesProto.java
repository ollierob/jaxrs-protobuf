package net.ollie.protobuf;

import java.io.IOException;
import java.io.OutputStream;

public interface WritesProto {

    void writeTo(OutputStream out) throws IOException;

}
