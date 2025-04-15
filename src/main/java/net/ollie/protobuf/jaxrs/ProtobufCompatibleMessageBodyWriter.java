package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import net.ollie.protobuf.BuildsProto;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<Class<?>, WriteFunction<?>> rawWriters = new ConcurrentHashMap<>();
    private final Map<Class<?>, WriteFunction<?>> genericWriters = new ConcurrentHashMap<>();

    public ProtobufCompatibleMessageBodyWriter() {
        this(false);
    }

    public ProtobufCompatibleMessageBodyWriter(final boolean writesString) {
        this.writesString = writesString;
    }

    public <T> void register(final Class<T> type, final WriteFunction<? super T> toProto) {
        rawWriters.put(type, toProto);
    }

    public <T> void registerCollection(final Class<T> genericType, final WriteFunction<? super Collection<T>> toCollectionProto) {
        genericWriters.put(genericType, toCollectionProto);
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return (isProtobufType(mediaType) || this.isWriteableString(mediaType))
                && (BuildsProto.class.isAssignableFrom(type) || rawWriters.containsKey(type) || this.isWriteableCollection(type, genericType));
    }

    private boolean isWriteableCollection(final Class<?> type, final Type genericType) {
        return Collection.class.isAssignableFrom(type)
                && genericWriters.containsKey(Types.readGenericTypeClass(genericType));
    }

    private boolean isWriteableString(final MediaType mediaType) {
        return writesString && MediaType.TEXT_PLAIN_TYPE.isCompatible(mediaType);
    }

    @Override
    public void writeTo(final Object object, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> multivaluedMap, final OutputStream outputStream) throws IOException {
        final var writer = this.getWriter(type, genericType);
        final var proto = writer.toProto(object);
        if (this.isWriteableString(mediaType)) outputStream.write(proto.toString().getBytes());
        else proto.writeTo(outputStream);
    }

    @Nonnull
    private WriteFunction getWriter(final Class<?> type, final Type genericType) {
        var writer = rawWriters.get(type);
        if (writer != null) return writer;
        if (BuildsProto.class.isAssignableFrom(type)) {
            writer = (WriteFunction<BuildsProto>) BuildsProto::toProto;
            rawWriters.putIfAbsent(type, writer);
            return writer;
        }
        writer = genericWriters.get(Types.readGenericTypeClass(genericType));
        return Objects.requireNonNull(writer);
    }

    public interface WriteFunction<T> {

        Message toProto(T object);

    }

}
