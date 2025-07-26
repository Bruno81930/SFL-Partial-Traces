package org.diagnosis.parser;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.parser.LogsParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogsParserTest {
    @Test
    public void test() {
        LogsParser logsParser = new LogsParser();
        HitVector hitVector = logsParser.parse("src/test/resources/full_project_samples/logs/BasicJobTest_testSimpleJob");
        assertEquals(24, hitVector.size());
    }
}
