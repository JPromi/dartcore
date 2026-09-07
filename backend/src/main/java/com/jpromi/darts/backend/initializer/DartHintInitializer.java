package com.jpromi.darts.backend.initializer;

import com.jpromi.darts.backend.entities.DartHint;
import com.jpromi.darts.backend.enums.DartThrowMultiplierEnum;
import com.jpromi.darts.backend.enums.GameTypeEnum;
import com.jpromi.darts.backend.repositories.DartHintRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class DartHintInitializer implements ApplicationRunner {

    private final DartHintRepository dartHintRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        var resource = new ClassPathResource("data/dart_checkout_hints.csv");

        try (
                var reader = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
                )
        ) {
            reader.lines()
                    .skip(1) // Skip header
                    .filter(line -> !line.isBlank())
                    .map(this::parseLine)
                    .forEach(dartHintRepository::save);
        }
    }

    private DartHint parseLine(String line) {
        String[] values = line.split(",", -1);

        return DartHint.builder()
                .id(Long.valueOf(values[0]))
                .gameType(GameTypeEnum.valueOf(values[1]))
                .gameTypeClassicOutType(
                        enumOrNull(DartThrowMultiplierEnum.class, values[2])
                )
                .points(Long.valueOf(values[3]))
                .t1Points(integerOrNull(values[4]))
                .t1Multiplier(enumOrNull(DartThrowMultiplierEnum.class, values[5]))
                .t2Points(integerOrNull(values[6]))
                .t2Multiplier(enumOrNull(DartThrowMultiplierEnum.class, values[7]))
                .t3Points(integerOrNull(values[8]))
                .t3Multiplier(enumOrNull(DartThrowMultiplierEnum.class, values[9]))
                .build();
    }

    private Integer integerOrNull(String value) {
        return value.isBlank() ? null : Integer.valueOf(value);
    }

    private <T extends Enum<T>> T enumOrNull(Class<T> type, String value) {
        return value.isBlank() ? null : Enum.valueOf(type, value);
    }
}
