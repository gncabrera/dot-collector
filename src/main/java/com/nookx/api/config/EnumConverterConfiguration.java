package com.nookx.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers a case-insensitive {@link String} → {@link Enum} converter factory so request params and
 * path variables typed as enums accept both {@code BUG} and {@code bug}.
 */
@Configuration
public class EnumConverterConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new CaseInsensitiveEnumConverterFactory());
    }

    private static final class CaseInsensitiveEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
            return new StringToEnum(targetType);
        }

        private static final class StringToEnum<T extends Enum<T>> implements Converter<String, T> {

            private final Class<T> enumType;

            StringToEnum(Class<T> enumType) {
                this.enumType = enumType;
            }

            @Override
            public T convert(String source) {
                if (source == null || source.isBlank()) {
                    return null;
                }
                return Enum.valueOf(enumType, source.trim().toUpperCase(java.util.Locale.ROOT));
            }
        }
    }
}
