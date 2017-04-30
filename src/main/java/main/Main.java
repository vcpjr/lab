package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Reporter;

import java.io.File;

public class Main {
	public static String PATH_DROPBOX_GGOES = "/Users/ggoes/Dropbox/Datasets (Vilmar)/";

    public static String PATH_DROPBOX_VILMAR = "/Users/ggoes/Dropbox/Datasets (Vilmar)/";
    public static String PATH_DROPBOX_TJ = "C:\\Users\\Vilmar\\Dropbox\\Datasets (Vilmar)\\";

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

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

        Reporter.execute(confidence, language, inputFile);
    }
}
