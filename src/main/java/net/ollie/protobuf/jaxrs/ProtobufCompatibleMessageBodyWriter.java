package net.ollie.protobuf.jaxrs;

import net.ollie.protobuf.WritesProto;

import javax.ws.rs.WebApplicationException;
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
public class ProtobufCompatibleMessageBodyWriter implements MessageBodyWriter<WritesProto> {

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return isProtobufType(mediaType)
                && WritesProto.class.isAssignableFrom(aClass);
    }

    @Override
    public void writeTo(WritesProto writesProto, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        writesProto.writeTo(outputStream);
    }

}
