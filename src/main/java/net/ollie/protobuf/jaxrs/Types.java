package net.ollie.protobuf.jaxrs;

import javax.annotation.CheckForNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

class Types {

    @CheckForNull
    static Class<?> readGenericTypeClass(final Type genericType) {
        if (!(genericType instanceof ParameterizedType)) return null;
        final var types = ((ParameterizedType) genericType).getActualTypeArguments();
        if (types.length != 1) return null;
        final var type = types[0];
        if (type instanceof Class) return (Class<?>) type;
        if (type instanceof WildcardType) {
            //TODO
            final var wildcardType = (WildcardType) type;
            if (wildcardType.getLowerBounds().length != 0) return null;
            final var upperBounds = wildcardType.getUpperBounds();
            return upperBounds.length == 1 && upperBounds[0] instanceof Class ? (Class<?>) upperBounds[0] : null;
        }
        return null;
    }

}
