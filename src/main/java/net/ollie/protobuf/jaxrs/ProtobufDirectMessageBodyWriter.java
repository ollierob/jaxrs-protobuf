package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;

import javax.ws.rs.Consumes;
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
    public void writeTo(final Message message, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        message.writeTo(outputStream);
    }

}
