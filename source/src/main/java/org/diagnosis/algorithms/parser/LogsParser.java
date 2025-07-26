package org.diagnosis.algorithms.parser;

import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsParser {

    private final Pattern logPattern;

    public LogsParser() {
        String logFormat = "%d{ABSOLUTE} %-5p %C %m%n";
        this.logPattern = createPatternFromFormat(logFormat);
    }

    private Pattern createPatternFromFormat(String logFormat) {
        String regex = logFormat
                .replace("%d{ABSOLUTE}", "(\\d{2}:\\d{2}:\\d{2},\\d{3})")
                .replace("%-5p", "(\\w+)")
                .replace("%C", "([\\w\\.]+)")
                .replace("%m%n", "(.*)");
        return Pattern.compile(regex);
    }

    public HitVector parse(String path) {
        HitVector hitVector = new HitVector();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = logPattern.matcher(line);
                if (matcher.find()) {
                    String fullyQualifiedName = matcher.group(3);
                    String packageName = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf("."));
                    String className = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
                    String methodName = matcher.group(4).split("\\(")[0];
                    if (Character.isUpperCase(methodName.charAt(0))) {
                        if (methodName.contains(".")) {
                            methodName = methodName.substring(methodName.lastIndexOf(".") + 1);
                        } else {
                            continue;
                        }
                    }
                    int numberOfHits = 1; // Assuming each log entry is a hit
                    Hit hit = new Hit(packageName, className, methodName, numberOfHits);
                    if (hit.isFromTestFile())
                        continue;

                    if (hitVector.contains(hit)) {
                        String finalMethodName = methodName;
                        hitVector.stream().filter(h -> h.getClassName().equals(className) && h.getMethodName().equals(finalMethodName)).findFirst().get().incrementNumberOfHits();
                    } else {
                        hitVector.add(hit);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hitVector;
    }
}
