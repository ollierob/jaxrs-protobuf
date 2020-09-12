package net.ollie.protobuf;

import com.google.protobuf.Message;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;

public interface BuildsProto<M extends Message> extends WritesProto {

    @Nonnull
    M toProto();

    @Override
    default void writeTo(final OutputStream out) throws IOException {
        this.toProto().writeTo(out);
    }

}
