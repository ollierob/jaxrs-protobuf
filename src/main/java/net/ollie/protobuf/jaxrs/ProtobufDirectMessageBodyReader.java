package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.ollie.protobuf.jaxrs.ProtobufMediaType.isProtobufType;

/**
 * Reads any {@link Message} type that has been {@link #register registered}:
 *
 * <pre>{@code
 *  @GET
 *  @Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
 *  MyProto read();
 * }
 * </pre>
 *
 * @see ProtobufDirectMessageBodyWriter
 * @see ProtobufCompatibleMessageBodyReader
 */
@Provider
@Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
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
                && (registeredBuilders.containsKey(type) || this.createDefaultParser(type));
    }

    private boolean createDefaultParser(final Class<?> type) {
        if (!Message.class.isAssignableFrom(type)) return false;
        try {
            final var parser = ParseFunction.defaultParser((Class<? extends Message>) type);
            registeredBuilders.put(type, parser);
            return true;
        } catch (final NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public Message readFrom(final Class<Message> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {
        return registeredBuilders.get(type).apply(entityStream);
    }

    public interface ParseFunction<T extends Message> {

        T apply(InputStream from) throws IOException;

        static <T extends Message> ParseFunction<T> defaultParser(final Class<T> type) throws NoSuchMethodException {
            final var method = type.getMethod("parseFrom", InputStream.class);
            return i -> {
                try {
                    //noinspection unchecked
                    return (T) method.invoke(type, i);
                } catch (final ReflectiveOperationException ex) {
                    throw new IOException(ex);
                }
            };
        }

    }

}
