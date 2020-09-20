package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static net.ollie.protobuf.jaxrs.ProtobufMediaType.isProtobufType;

/**
 * Writes any object type that has been {@link #register registered}.
 */
@Provider
@Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
public class ProtobufCompatibleMessageBodyWriter implements MessageBodyWriter<Object> {

    private final Map<Class<?>, WriteFunction<?>> writers = new HashMap<>();

    public <T> void register(final Class<T> type, final WriteFunction<? super T> toProto) {
        writers.put(type, toProto);
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return isProtobufType(mediaType)
                && writers.containsKey(type);
    }

    @Override
    public void writeTo(final Object object, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> multivaluedMap, final OutputStream outputStream) throws IOException {
        final WriteFunction writer = writers.get(type);
        writer.toProto(object).writeTo(outputStream);
    }

    public interface WriteFunction<T> {

        Message toProto(T object);

    }

}
