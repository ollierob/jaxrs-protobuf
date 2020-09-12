package net.ollie.protobuf.jaxrs;

import net.ollie.protobuf.BuildsProto;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ProtobufStringMessageBodyWriter implements MessageBodyWriter<BuildsProto<?>> {

    @Override
    public boolean isWriteable(final Class<?> aClass, final Type type, final Annotation[] annotations, final MediaType mediaType) {
        return MediaType.TEXT_PLAIN_TYPE.isCompatible(mediaType)
                && BuildsProto.class.isAssignableFrom(aClass);
    }

    @Override
    public void writeTo(final BuildsProto<?> buildsProto, final Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        final var proto = buildsProto.toProto();
        outputStream.write(proto.toString().getBytes());
    }

}
