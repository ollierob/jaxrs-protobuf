package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static net.ollie.protobuf.jaxrs.ProtobufMediaType.isProtobufType;

/**
 * Writes any actual protobuf {@link Message}:
 * <pre>{@code
 *  @POST
 *  @Consumes(ProtobufMediaType.APPLICATION_PROTOBUF)
 *  Response post(MyProto proto);
 * }
 * </pre>
 *
 * @see ProtobufDirectMessageBodyReader
 */
@Provider
@Consumes(ProtobufMediaType.APPLICATION_PROTOBUF)
public class ProtobufDirectMessageBodyWriter implements MessageBodyWriter<Message> {

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return isProtobufType(mediaType)
                && Message.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(final Message message, final Class<?> aClass, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> multivaluedMap, final OutputStream outputStream) throws IOException {
        message.writeTo(outputStream);
    }

}
