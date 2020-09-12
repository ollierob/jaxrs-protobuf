package net.ollie.protobuf.jaxrs;

import net.ollie.protobuf.WritesProto;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static net.ollie.protobuf.jaxrs.ProtobufMediaType.isProtobufType;

/**
 * Writes any implementation of {@link WritesProto}.
 */
@Provider
@Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
public class ProtobufCompatibleMessageBodyWriter implements MessageBodyWriter<WritesProto> {

    @Override
    public boolean isWriteable(final Class<?> aClass, final Type type, final Annotation[] annotations, final MediaType mediaType) {
        return isProtobufType(mediaType)
                && WritesProto.class.isAssignableFrom(aClass);
    }

    @Override
    public void writeTo(final WritesProto writesProto, Class<?> aClass, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> multivaluedMap, final OutputStream outputStream) throws IOException {
        writesProto.writeTo(outputStream);
    }

}
