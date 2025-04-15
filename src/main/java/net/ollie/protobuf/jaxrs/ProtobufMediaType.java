package net.ollie.protobuf.jaxrs;

import jakarta.ws.rs.core.MediaType;

public class ProtobufMediaType {

    public static final String APPLICATION_PROTOBUF = "application/protobuf";
    public static final MediaType APPLICATION_PROTOBUF_TYPE = new MediaType("application", "protobuf");

    public static boolean isProtobufType(final MediaType type) {
        return APPLICATION_PROTOBUF_TYPE.isCompatible(type);
    }

}
