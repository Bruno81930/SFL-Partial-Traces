package org.diagnosis.app.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.DynamicConverter;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MemoryUsageConverter extends DynamicConverter<ILoggingEvent> {
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        String usedMemory = formatMemory(heapUsage.getUsed());
        String maxMemory = formatMemory(heapUsage.getMax());
        return "\u001B[32m Used heap memory: " + usedMemory + "\u001B[0m, \u001B[31m Max heap memory: " + maxMemory + "\u001B[0m";
    }

    public String formatMemory(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp-1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
