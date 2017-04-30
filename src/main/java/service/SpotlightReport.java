package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.dbpediaspotlight.Annotation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SpotlightReport {

    private final String outputFilePath;
    private final String header;
    private List<String> reportBody;
    private static final Logger LOG = LoggerFactory.getLogger(SpotlightReport.class);

    public SpotlightReport(String outputFilePath) {
        this.outputFilePath = outputFilePath;
        this.header = "Tweet|Confidence|Annotation|URI|Classes|Time To Request";
        reportBody = new ArrayList<>();
    }

    public void append(Annotation annotation, long durationInMs) {

        annotation.getResources().forEach(resources ->
            resources.getTypes().forEach((String namespace, Set<String> classSet) -> {
                for (String classType : classSet) {
                    // Tweet,Confidence,Annotation,URI,Classes,Time To Request
                    final String annotatedData = String.format(Locale.US, "%s|%.2f|%s|%s|%s|%d\n",
                        annotation.getText(), annotation.getConfidence(), resources.getSurfaceForm(),
                        resources.getURI(), namespace + ":" + classType, durationInMs);
                    reportBody.add(annotatedData);
                }
            })
        );
    }

    public final int getBodyLineSize() {
        return reportBody.size();
    }

    public void reporting() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFilePath);
            writer.write(header + "\n");
            writer.write(reportBody.stream().collect(Collectors.joining("")));
        } catch (IOException e) {
            LOG.error("Error to report '" + outputFilePath + "' file.", e);
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                LOG.error("Unknown exception.", e);
                e.printStackTrace();
            }
        }
    }


}
