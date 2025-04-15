package net.ollie.protobuf.jaxrs;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import net.ollie.protobuf.BuildsProto;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Writes any implementation of {@link BuildsProto} to {@link com.google.protobuf.Message#toString string}.
 * Mainly used for debugging output.
 */
@Provider
@Produces(MediaType.TEXT_PLAIN)
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
