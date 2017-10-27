package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.NerdExecutor;

import java.io.File;

import static java.lang.System.exit;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Parameters not found. For run this application use follow command.");
            System.out.println("./run.sh <dataset> <confidence_level> <language>\n");
            exit(1);
        }

        LOG.info("Starting Application.");
        String filePath = args[0];
        float confidence = Float.parseFloat(args[1]);
        String language = args[2];
        File inputFile = new File(filePath);

        if (!inputFile.exists()) {
            LOG.error("File not found!");
            System.err.println("File not found!");
            exit(1);
        }

        NerdExecutor nerdExecutor = new NerdExecutor(inputFile);
        nerdExecutor.execute(confidence, language);
    }
}
