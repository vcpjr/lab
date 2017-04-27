package main;

import dao.TweetDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Tweet;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class Main {
	public static String PATH_DROPBOX_GGOES = "/Users/ggoes/Dropbox/Datasets (Vilmar)/";

	public static String PATH_DROPBOX_VILMAR = "/Users/ggoes/Dropbox/Datasets (Vilmar)/";
	public static String PATH_DROPBOX_TJ = "C:\\Users\\Vilmar\\Dropbox\\Datasets (Vilmar)\\";

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	private static TweetDAO tweetDAO = new TweetDAO();
	private static String[] languages = { "pt", "en" };

	public static void main(String[] args) {

	    LOG.info("Starting Application.");
		String filePath = args[0];
		File inputFile = new File(filePath);

		if (!inputFile.exists()) {
            LOG.error("File not found!");
            System.err.println("File not found!");
            System.exit(1);
        }


		callSpotlight();

		countMentions();
	}

	private static void countMentions() {

		for (int i = 0; i < languages.length; i++) {
			String language = languages[i];

			for (double confidence = 0.05; confidence <= 1.0; confidence += 0.05) {
				File inputFile = new File(PATH_DROPBOX_TJ + "outputSpotlight_dataset_Fabio_Bif_lang=" + language
						+ "_confidence=" + confidence + ".txt");
				File outputFile = new File(PATH_DROPBOX_TJ + "countSpotlight_dataset_Fabio_Bif_lang=" + language
						+ "en_confidence=" + confidence + ".csv");

				FileReader fr;
				try {
					fr = new FileReader(inputFile);
					BufferedReader br = new BufferedReader(fr);
					FileWriter fw = new FileWriter(outputFile, true);
					BufferedWriter bw = new BufferedWriter(fw);

					// TODO
					bw.write(
							"#;Num. Annotations; Num. Annotations e-Commerce; Num./Instance; Num. Classes; Time;Tweet \n");
					int count = 1;
					while (br.ready()) {
						String line = br.readLine();

						countLine(line, bw, count++);
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void countLine(String line, BufferedWriter bw, int count) {

		// TODO tratar a linha da menção encontrada
		String auxLine = line;
		String[] s = line.split("@text");

		try {
			bw.write(count + ";");
			if (s != null && s.length > 1) {
				s = s[1].split(",");
				String text = s[0];

				int nAnnotations = (auxLine.length() - auxLine.replaceAll("@URI", "").length()) / "@URI".length();

				bw.write(text + ";" + nAnnotations + "; \n");// + classes);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void callSpotlight() {

		int size = 1;
		List<Tweet> tweets = tweetDAO.getRandomList(size);

		for (int i = 0; i < languages.length; i++) {
			String language = languages[i];
			// for (double confidence = 0.05; confidence <= 1.0; confidence +=
			// 0.05) {
			for (double confidence = 0.15; confidence <= 1.0; confidence += 0.05) {

				logger.log(Level.INFO, "Running Spotlight: lang=" + language + ". confidence=" + confidence);

				String path = PATH_DROPBOX_TJ + "outputSpotlight_dataset_Fabio_Bif_lang=" + language + "_confidence="
						+ confidence + ".txt";
				FileWriter fw = null;
				try {
					fw = new FileWriter(path);
					for (Tweet t : tweets) {
						String spotlightResp = getSpotlightResponse(t.getMessage(), confidence, language);
						fw.write(spotlightResp + "\n");

					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private static String getSpotlightResponse(String text, double confidence, String language) {

		String output = "";
		// logger.log(Level.INFO, "Spotting text: " + text);

		text = text.replace(' ', '+');
		try {
			URL url = new URL("http://model.dbpedia-spotlight.org/" + language + "/annotate?confidence=" + confidence
					+ "&text=" + text);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException(
						"Failed : HTTP error code : " + conn.getResponseCode() + ": " + conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			// System.out.println("Output from Server .... \n");
			while (br.ready()) {
				output += br.readLine();
			}
			System.out.println(output);
			conn.disconnect();
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return output;
	}
}
