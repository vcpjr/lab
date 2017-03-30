package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dao.TweetDAO;
import entity.Tweet;

public class Main {

	public static String PATH_DROPBOX_GGOES = "/Users/ggoes/Dropbox/Datasets/";
	public static String PATH_DROPBOX_VILMAR = "/Users/ggoes/Dropbox/Datasets/";
	public static String PATH_DROPBOX_TJ = "C:\\Users\\Vilmar\\Dropbox\\Datasets (Vilmar)\\";
	private static Logger logger;

	private static TweetDAO tweetDAO = new TweetDAO();
	private static String[] languages = { "en", "pt" };

	public static void main(String[] args) {

		logger = Logger.getLogger("LabLOG");

		logger.log(Level.INFO, "Iniciando o Lab");

		// callFox();
		callSpotlight();

		countMentions();
	}

	private static void countMentions() {

		for (double confidence = 0.05; confidence <= 1.0; confidence += 0.05) {
			File inputFile = new File(
					PATH_DROPBOX_TJ + "outputSpotlight_dataset_Fabio_Bif_lang=en_confidence=" + confidence + ".txt");
			File outputFile = new File(
					PATH_DROPBOX_TJ + "countSpotlight_dataset_Fabio_Bif_lang=en_confidence=" + confidence + ".txt");

			FileReader fr;
			try {
				fr = new FileReader(inputFile);
				BufferedReader br = new BufferedReader(fr);
				FileWriter fw = new FileWriter(outputFile, true);
				BufferedWriter bw = new BufferedWriter(fw);

				while (br.ready()) {
					String line = br.readLine();

					countLine(line, bw);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void countLine(String line, BufferedWriter bw) {

		// TODO tratar a linha da menção encontrada
		String[] s = line.split("Resources:");

		int mentions = 0;
		int count = 0;
		try {
			bw.write(count + ": ");
			if (s != null && s.length > 2) {
				s = s[1].split("@URI");
				mentions = s.length;
				bw.write("mentions: " + mentions);
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO usar biblioteca de JSON??
		// Gson parser = new Gson();
		// parser.fromJson(line, Resource);

	}

	private static void callFox() {
		// logger.log(Level.INFO, "Running FOX");
		// String path =
		// "/Users/ggoes/Dropbox/Datasets/outputFOX_dataset_Fabio_Bif.txt";
		// FileWriter fw = null;
		// try {
		// fw = new FileWriter(path);
		// for (Tweet t : tweets) {
		// FoxResponse foxResp = getFoxResponse(t.getMessage());
		//
		// fw.write(foxResp.getOutput());
		// System.out.println(foxResp.getInput());
		// // System.out.println(foxResp.getOutput());
		// System.out.println(foxResp.getLog());
		//
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// fw.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// logger.log(Level.INFO, "End FOX");
	}

	private static void callSpotlight() {

		int size = 1;
		List<Tweet> tweets = tweetDAO.getRandomList(size);

		for (int i = 0; i < languages.length; i++) {
			String language = languages[i];
			for (double confidence = 0.05; confidence <= 1.0; confidence += 0.05) {

				logger.log(Level.INFO, "Running Spotlight: lang=" + language + ". confidence=" + confidence);

				String path = PATH_DROPBOX_TJ + "outputSpotlight_dataset_Fabio_Bif_2_lang=" + language + "_confidence="
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
		logger.log(Level.INFO, "Spotting text: " + text);

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

			System.out.println("Output from Server .... \n");
			while (br.ready()) {
				output += br.readLine();
			}
			System.out.println(output);
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;

	}

	// private static FoxResponse getFoxResponse(String text) {
	//
	// IFoxApi fox = new FoxApi();
	//
	// // URL api = new URL("http://0.0.0.0:4444/api");
	// // fox.setApiURL(api);
	//
	// fox.setTask(FoxParameter.TASK.NER);
	// fox.setOutputFormat(FoxParameter.OUTPUT.RDFXML);
	// fox.setLang(FoxParameter.LANG.EN); // Opcoes: EN, ES, DE, FR, NL
	//
	// // Variar as entradas
	// fox.setInput(text);
	// // fox.setLightVersion(FoxParameter.FOXLIGHT.ENStanford);
	//
	// FoxResponse response = fox.send();
	//
	// return response;
	// }

}
