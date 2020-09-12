package net.ollie.protobuf;

import com.google.protobuf.ProtocolMessageEnum;

import javax.annotation.Nonnull;

public interface BuildsProtoEnum<E extends ProtocolMessageEnum> {

    @Nonnull
    E toProto();

}
