package main;

import java.io.BufferedReader;
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

	private static Logger logger;
	private static TweetDAO tweetDAO = new TweetDAO();

	public static void main(String[] args) {

		logger = Logger.getLogger("LabLOG");

		logger.log(Level.INFO, "Iniciando o Lab");

		// logger.log(Level.INFO, "Running FOX");
		//
		int size = 1;
		List<Tweet> tweets = tweetDAO.getRandomList(size);
		//
		String path = "/Users/vilmar-macbook-air/git/lab/datasets/outputFOX_dataset_Fabio_Bif.txt";
		FileWriter fw = null;
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

		logger.log(Level.INFO, "Running Spotlight");

		path = "/Users/vilmar-macbook-air/git/lab/datasets/outputSpotlight_dataset_Fabio_Bif.txt";
		fw = null;
		try {
			fw = new FileWriter(path);
			for (Tweet t : tweets) {
				String spotlightResp = getSpotlightResponse(t.getMessage());

				fw.write(spotlightResp);

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

	private static String getSpotlightResponse(String text) {

		String output = "";

		try {
			// TODO verificar os parâmetros
			// (idioma PT)
			// nível de confiança (0.1)

			// TESTES
			// URL url = new
			// URL("http://spotlight.sztaki.hu:2228/rest/annotate?text=" +
			// text);

			URL url = new URL(" http://spotlight.sztaki.hu:2228/rest/annotate?text=" + text);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
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
