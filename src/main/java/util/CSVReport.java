package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CSVReport {
    private static final Logger LOG = LoggerFactory.getLogger(CSVReport.class);

    private List<String[]> body;

    /**
     * CSVReport is a builder for csv file
     */
    private String delimiter = ";";

    /**
     * CSVReport use ";" token as default if delimiter is not passed
     * @param header    strings with the identity fields in file
     */
    public CSVReport(String... header) {
        body = new ArrayList<>();
        this.append(header);
    }

    /**
     * Get quantity of lines in file
     * @return lines size
     */
    public final int getLinesSize() {
        return body.size();
    }

    /**
     * Define a delimiter token
     * if is not defined use ";" as default.
     *
     * @param delimiter used to separate fields in csv file
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Append a line to file with content passed delimited by @delimiter configured
     * @param content arguments for each field of header
     */
    public void append(String... content) {
        body.add(content);
    }

    /**
     * Generate an output csv file passed as parameter
     * @param outputFile path and name of the file
     */
    public void generate(File outputFile) {
        FileWriter writer = null;
        try {
            if (outputFile.getParentFile().mkdirs())
                LOG.info("Directory '" + outputFile.getParentFile() + "' created.");

            writer = new FileWriter(outputFile);
            writer.write(
                body.stream()
                    .map(line -> String.join(delimiter, line))
                    .collect(Collectors.joining("\n")));
        } catch (IOException e) {
            LOG.error("Error to report '" + outputFile + "' file.", e);
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
        LOG.info("Reported file '" + outputFile.getPath() + "' created.");
    }
}
