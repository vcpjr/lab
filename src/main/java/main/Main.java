package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aksw.fox.binding.java.FoxParameter;
import org.aksw.fox.binding.java.FoxResponse;
import org.aksw.fox.binding.java.IFoxApi;
import org.apache.jena.atlas.json.io.parserjavacc.javacc.JSON_Parser;
import org.aksw.fox.binding.java.FoxApi;

import dao.TweetDAO;
import entity.Tweet;

public class Main {

	private static Logger logger;
	private static TweetDAO tweetDAO = new TweetDAO();

	public static void main(String[] args) {

		logger = Logger.getLogger("LabLOG");

		logger.log(Level.INFO, "Iniciando o Lab");
//		FoxResponse foxResp = getFoxResponse();
//
//		System.out.println(foxResp.getInput());
//		System.out.println(foxResp.getOutput());
//		System.out.println(foxResp.getLog());
		
		
		// TODO chamar spotlight

		getSpotlightResponse();
	}

	private static void getSpotlightResponse() {
		try {

			String texto = getTexto();
			URL url = new URL("http://spotlight.dbpedia.org/rest/annotate?text=" + texto);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static FoxResponse getFoxResponse() {

		IFoxApi fox = new FoxApi();

		//URL api = new URL("http://0.0.0.0:4444/api");
		//fox.setApiURL(api);

		fox.setTask(FoxParameter.TASK.NER);
		fox.setOutputFormat(FoxParameter.OUTPUT.RDFXML);
		fox.setLang(FoxParameter.LANG.EN); // Opcoes: EN, ES, DE, FR, NL

		// Variar as entradas
		fox.setInput(getTexto());
		// fox.setLightVersion(FoxParameter.FOXLIGHT.ENStanford);

		FoxResponse response = fox.send();		


		return response;
	}

	private static String getTexto() {
		// TODO conectar aos datasets de tweets
		int size = 20;
		ArrayList<Tweet> tweets = tweetDAO.getRandomList(size);

		return "The philosopher and mathematician Leibniz was born in Leipzig"; //TODO teste
	}

}
