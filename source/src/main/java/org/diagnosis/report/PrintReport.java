package org.diagnosis.report;

import org.diagnosis.algorithms.filter.FilterStrategy;
import org.diagnosis.algorithms.sfl.ssfl.Formula;
import org.diagnosis.evaluation.Metric;
import org.diagnosis.evaluation.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintReport implements Report{

    Logger logger = LoggerFactory.getLogger(PrintReport.class);
    Metrics metrics;
    Formula formula;
    FilterStrategy filterStrategy;

    public PrintReport(Metrics metrics, Formula formula, FilterStrategy filterStrategy) {
        this.metrics = metrics;
        this.formula = formula;
        this.filterStrategy = filterStrategy;
    }

    @Override
    public void report() {
        metrics.calculateAll();
        logger.info("Printing the metrics for filter strategy: {}", filterStrategy.getName());
        for (Metric metric : metrics) {
            logger.info("\u001B[33m{} \u001B[0m <==> \u001B[4m{}\u001B[0m: \u001B[1m{}\u001B[0m",  formula.getFormula().getName(), metric.getName(), String.format("%.3f", metric.getValue()));
        }
    }
}
