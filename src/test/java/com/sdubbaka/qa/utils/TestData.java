package com.sdubbaka.qa.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/** Loads JSON test data from src/test/resources/testdata into TestNG DataProvider rows. */
public final class TestData {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestData() {}

    public static Object[][] rows(String file) {
        try (InputStream in = TestData.class.getClassLoader().getResourceAsStream("testdata/" + file)) {
            List<Map<String, String>> list = MAPPER.readValue(in, new TypeReference<>() {});
            return list.stream().map(m -> new Object[] {m}).toArray(Object[][]::new);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read test data " + file, e);
        }
    }
}
