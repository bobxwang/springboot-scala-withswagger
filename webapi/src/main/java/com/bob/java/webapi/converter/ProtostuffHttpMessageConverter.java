package com.bob.java.webapi.converter;

import com.google.common.base.Stopwatch;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.xerial.snappy.SnappyOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

/**
 * Created by bob on 17/1/17.
 */
public class ProtostuffHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final Logger logger = LoggerFactory.getLogger(ProtostuffHttpMessageConverter.class);

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final MediaType MEDIA_TYPE = new MediaType("application", "x-protobuf", DEFAULT_CHARSET);
    public static final MediaType MEDIA_TYPE_GZIP = new MediaType("application", "x-protobuf-gzip", DEFAULT_CHARSET);
    public static final MediaType MEDIA_TYPE_SNAPPY = new MediaType("application", "x-protobuf-snappy", DEFAULT_CHARSET);
    public static final String X_PROTOBUF_SCHEMA_HEADER = "X-Protobuf-Schema";
    public static final String X_PROTOBUF_MESSAGE_HEADER = "X-Protobuf-Message";

    public ProtostuffHttpMessageConverter() {
        super(MEDIA_TYPE, MEDIA_TYPE_GZIP, MEDIA_TYPE_SNAPPY);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        return mediaType.isCompatibleWith(MEDIA_TYPE);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        return mediaType.isCompatibleWith(MEDIA_TYPE);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // Should not be called, since we override canRead/canWrite.
        throw new UnsupportedOperationException();
    }

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(final Class<T> clasz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clasz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clasz);
            if (schema != null) {
                cachedSchema.put(clasz, schema);
            }
        }
        return schema;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (MEDIA_TYPE.isCompatibleWith(contentType)) {
            final Schema<?> schema = getSchema(clazz);
            final Object value = schema.newMessage();

            try (final InputStream stream = inputMessage.getBody()) {
                ProtobufIOUtil.mergeFrom(stream, value, (Schema<Object>) schema);
                return value;
            }
        }

        throw new HttpMessageNotReadableException(
                "Unrecognized HTTP media type " + inputMessage.getHeaders().getContentType().getType() + ".");
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        logger.info("Current type: {}", outputMessage.getHeaders().getContentType());
        Stopwatch stopwatch = Stopwatch.createStarted();
        OutputStream stream = null;

        try {
            if (MEDIA_TYPE.isCompatibleWith(outputMessage.getHeaders().getContentType())) {
                outputMessage.getHeaders().set(X_PROTOBUF_SCHEMA_HEADER, o.getClass().getSimpleName());
                outputMessage.getHeaders().set(X_PROTOBUF_MESSAGE_HEADER, o.getClass().getName());
                stream = outputMessage.getBody();
            } else if (MEDIA_TYPE_GZIP.isCompatibleWith(outputMessage.getHeaders().getContentType())) {
                stream = new GZIPOutputStream(stream);
            } else if (MEDIA_TYPE_SNAPPY.isCompatibleWith(outputMessage.getHeaders().getContentType())) {
                stream = new SnappyOutputStream(stream);
            } else {
                throw new HttpMessageNotWritableException(
                        "Unrecognized HTTP media type " + outputMessage.getHeaders().getContentType().getType() + ".");
            }

            ProtobufIOUtil.writeTo(stream, o, getSchema((Class<Object>) o.getClass()),
                    LinkedBuffer.allocate());
            stream.flush();
        } finally {
            IOUtils.closeQuietly(stream);
        }

        logger.info("Output spend {}", stopwatch.toString());
    }
}