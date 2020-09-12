package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;

import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.ollie.protobuf.jaxrs.ProtobufMediaType.isProtobufType;

/**
 * Reads any {@link Message} type that has been {@link #register registered}.
 *
 * @see ProtobufDirectMessageBodyWriter
 */
@Provider
public class ProtobufDirectMessageBodyReader implements MessageBodyReader<Message> {

    private final Map<Class<?>, ParseFunction<? extends Message>> registeredBuilders = new HashMap<>();

    /**
     * Register a reader. The second argument is usually a method-reference to the class' {@code parseFrom} method.
     */
    public <T extends Message> void register(@Nonnull final Class<T> type, @Nonnull final ParseFunction<T> parseFrom) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(parseFrom, "parseFrom");
        registeredBuilders.put(type, parseFrom);
    }

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return isProtobufType(mediaType)
                && registeredBuilders.containsKey(type);
    }

    @Override
    public Message readFrom(final Class<Message> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {
        return registeredBuilders.get(type).apply(entityStream);
    }

    public interface ParseFunction<T extends Message> {

        T apply(InputStream from) throws IOException;

    }

}
