package main;

import java.util.ArrayList;

import org.aksw.fox.binding.java.FoxParameter;
import org.aksw.fox.binding.java.FoxResponse;
import org.aksw.fox.binding.java.IFoxApi;

import dao.TweetDAO;
import entity.Tweet;

public class Main {

	private static TweetDAO tweetDAO = new TweetDAO();

	public static void main(String[] args) {

		FoxResponse foxResp = getFoxResponse();

		// TODO chamar spotlight
	}

	private static FoxResponse getFoxResponse() {
		IFoxApi fox = new FoxAPI();

		fox.setTask(FoxParameter.TASK.NER);
		fox.setOutputFormat(FoxParameter.OUTPUT.JSONLD);
		fox.setLang(FoxParameter.LANG.EN); // Opcoes: EN, ES, DE, FR, NL

		// Variar as entradas
		fox.setInput(getTexto());

		FoxResponse response = fox.send();
		return response;
	}

	private static String getTexto() {
		// TODO conectar aos datasets de tweets
		int size = 20;
		ArrayList<Tweet> tweets = tweetDAO.getRandomList(size);

		return "The philosopher and mathematician Leibniz was born in Leipzig";
	}

}
