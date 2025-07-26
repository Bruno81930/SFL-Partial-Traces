package org.diagnosis.parser;

import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.parser.ExecutionsParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExecutionsParserTest {
    @Test
    public void test() {
        ExecutionsParser executionsParser = new ExecutionsParser();
        HitVector hitVector = executionsParser.parse("src/test/resources/full_project_samples/traces/BasicJobTest_testSimpleJob.xml");
        assertEquals(3891, hitVector.size());
    }
}
