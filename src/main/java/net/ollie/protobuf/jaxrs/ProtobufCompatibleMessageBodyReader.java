package net.ollie.protobuf.jaxrs;

import com.google.protobuf.Message;

import javax.ws.rs.Produces;
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
import java.util.function.Function;

import static net.ollie.protobuf.jaxrs.ProtobufMediaType.isProtobufType;

/**
 * Reads any object type that has been {@link #register registered}.
 */
@Provider
@Produces(ProtobufMediaType.APPLICATION_PROTOBUF)
public class ProtobufCompatibleMessageBodyReader implements MessageBodyReader<Object> {

    private final Map<Class<?>, Reader<?, ?>> readers = new HashMap<>();

    public <T, M extends Message> void register(final Class<T> type, final ProtobufDirectMessageBodyReader.ParseFunction<M> parser, final Function<? super M, ? extends T> converter) {
        readers.put(type, new Reader<>(parser, converter));
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isProtobufType(mediaType)
                && readers.containsKey(type);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        return readers.get(type).parse(entityStream);
    }

    private static class Reader<T, M extends Message> {

        private final ProtobufDirectMessageBodyReader.ParseFunction<M> parser;
        private final Function<? super M, ? extends T> converter;

        private Reader(ProtobufDirectMessageBodyReader.ParseFunction<M> parser, Function<? super M, ? extends T> converter) {
            this.parser = parser;
            this.converter = converter;
        }

        T parse(final InputStream stream) throws IOException {
            return converter.apply(parser.apply(stream));
        }

    }

}
