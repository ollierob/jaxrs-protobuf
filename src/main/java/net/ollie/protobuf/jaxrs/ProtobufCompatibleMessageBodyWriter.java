package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;
import net.ollie.protobuf.BuildsProto;

import javax.annotation.Nonnull;
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
import java.util.Objects;

import static net.ollie.protobuf.jaxrs.ProtobufMediaType.isProtobufType;

/**
 * Writes any object type that has been {@link #register registered}.
 * <p>
 * Note that some JAXRS implementations will ignore duplicate instances of this class.
 * You should consider registering subclasses of this class.
 */
@Provider
@Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
public class ProtobufCompatibleMessageBodyWriter implements MessageBodyWriter<Object> {

    private final boolean writesString;
    private final Map<Class<?>, WriteFunction<?>> writers = new HashMap<>();

    public ProtobufCompatibleMessageBodyWriter() {
        this(false);
    }

    public ProtobufCompatibleMessageBodyWriter(final boolean writesString) {
        this.writesString = writesString;
    }

    public <T> void register(final Class<T> type, final WriteFunction<? super T> toProto) {
        writers.put(type, toProto);
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return (isProtobufType(mediaType) || this.isWriteableString(mediaType))
                && (writers.containsKey(type) || BuildsProto.class.isAssignableFrom(type));
    }

    private boolean isWriteableString(final MediaType mediaType) {
        return writesString && MediaType.TEXT_PLAIN_TYPE.isCompatible(mediaType);
    }

    @Override
    public void writeTo(final Object object, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> multivaluedMap, final OutputStream outputStream) throws IOException {
        final var writer = this.getWriter(type);
        final var proto = writer.toProto(object);
        if (this.isWriteableString(mediaType)) outputStream.write(proto.toString().getBytes());
        else proto.writeTo(outputStream);
    }

    @Nonnull
    private WriteFunction getWriter(final Class<?> type) {
        var writer = writers.get(type);
        if (writer == null && BuildsProto.class.isAssignableFrom(type)) {
            writer = (WriteFunction<BuildsProto>) BuildsProto::toProto;
            writers.putIfAbsent(type, writer);
        }
        return Objects.requireNonNull(writer);
    }

    public interface WriteFunction<T> {

        Message toProto(T object);

    }

}
