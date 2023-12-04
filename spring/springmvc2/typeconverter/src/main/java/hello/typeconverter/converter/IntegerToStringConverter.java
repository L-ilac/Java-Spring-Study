package hello.typeconverter.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntegerToStringConverter implements Converter<Integer, String> {
    @Override
    @Nullable
    public String convert(Integer source) {
        log.info("convert source = {}", source);
        return String.valueOf(source);

    }

}