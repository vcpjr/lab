package service;

import org.junit.Assert;
import org.junit.Test;
import pojo.dbpediaspotlight.Annotation;
import pojo.dbpediaspotlight.AnnotationResource;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class SpotlightReportTest {


    @Test
    public void testRepoting() {
        LocalTime start = LocalTime.now();

        ClassLoader classLoader = getClass().getClassLoader();
        String outputFilePath = classLoader.getResource("test").getPath() + "/ReportingTest.csv";
        SpotlightReport report = new SpotlightReport(outputFilePath);

        List<AnnotationResource> resources = Collections.singletonList(new AnnotationResource(
            "http://dbpedia.org/resource/Dell",
            1943,
            new HashMap<String, Set<String>>() {{
                put("Schema", new HashSet<String>() {{
                    add("Organization");
                }});
                put("DBpedia", new HashSet<String>(Arrays.asList("Agent", "Company", "Organisation")));
            }},
            "Dell",
            0,
            0.9999999862765493,
            8.873429642287647E-9));

        Annotation ann = new Annotation(
            "Dell XPS 13 13.3\" QHD+ IPS Touchscreen Notebook Core i5 8GB Ram 256GB SSD 2.3GHz",
            0.05f,
            0,
            "",
            "",
            "whitelist",
            resources
        );

        LocalTime end = LocalTime.now();
        long duration = start.until(end, ChronoUnit.MILLIS);
        report.append(ann, duration);

        Assert.assertEquals(4, report.getBodyLineSize());
    }
}
