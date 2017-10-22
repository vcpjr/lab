package util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class CSVReportTest {

    private CSVReport report;
    final String tmpPath = System.getProperty("java.io.tmpdir");

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Before
    public void setUp() {
        report = new CSVReport("numberField", "nameField");
    }

    @Test
    public void testCSVContentInMemory() throws IOException {
        report.append("1", "an_name");
        report.append("2", "other_name");

        Assert.assertEquals(3, report.getLinesSize());
    }

    @Test
    public void testCSVWithDefaultDelimiter() throws IOException {
        report.append("1", "an_name");
        report.append("2", "other_name");

        File outputFile = tmpDir.newFile();
        report.generate(outputFile);
        Assert.assertEquals(Files.readAllLines(outputFile.toPath()), Arrays.asList(
            "numberField;nameField",
            "1;an_name",
            "2;other_name"));
    }

    @Test
    public void testCSVWithCommaDelimiter() throws IOException {
        report.append("1", "an_name");
        report.append("2", "other_name");

        File outputFile = tmpDir.newFile();
        report.setDelimiter(",");
        report.generate(outputFile);
        Assert.assertEquals(Files.readAllLines(outputFile.toPath()), Arrays.asList(
            "numberField,nameField",
            "1,an_name",
            "2,other_name"));
    }
}
