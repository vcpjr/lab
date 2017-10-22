package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.NerdExecutor;

import java.io.File;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        LOG.info("Starting Application.");
        String filePath = args[0];
        float confidence = Float.parseFloat(args[1]);
        String language = args[2];
        File inputFile = new File(filePath);

        if (!inputFile.exists()) {
            LOG.error("File not found!");
            System.err.println("File not found!");
            System.exit(1);
        }

        NerdExecutor nerdExecutor = new NerdExecutor(inputFile);
        nerdExecutor.execute(confidence, language);
    }
}
