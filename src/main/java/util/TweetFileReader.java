package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.TweetDAO;
import pojo.Tweet;

public class TweetFileReader {
	private static final Logger LOG = LoggerFactory.getLogger(TweetFileReader.class);
	private static XSSFWorkbook xssfWorkbook;

	public static List<Tweet> readTweetsFromTxtFile(File file) {
		List<Tweet> tweets = null;
		String filename = file.getName();
		TweetDAO dao = new TweetDAO();
		try {
			LOG.info("Reading tweets from '" + filename + "' file.");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			tweets = new ArrayList<>();

			//TODO dataset do Fábio Bif só tem os textos -> Ids gerados manualmente
			//Long id, Long userId, String text, Date creationDate, boolean isRetweet
			long idTweet = 1;		
			long idUser = 1;

			while (br.ready()) {
				//TODO alterar aqui para pegar os demais campos do tweet (lendo do JSON)
				//tweets.add(JsonConverter.toTweet(normalize(br.readLine())));
				String text = br.readLine();
				boolean isRT = (text != null) && (text.contains("RT"));

				//Ou usar a linha seguinte caso vá ler o .txt com apenas o tweet em si (dataset_Fabio_bif)
				Tweet t = new Tweet(idTweet++, idUser++, text, new Date(), isRT);
				
				LOG.info("Reading tweet: '" + t.toString());
				
				tweets.add(t);
				dao.insert(t);
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			LOG.error("File '" + filename + "' not found");
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("Unknown Exception.");
			e.printStackTrace();
		}

		LOG.info("Read " + tweets.size() + " tweets from file.");
		return tweets;
	}

	public static List<Tweet> readTweetsFromPepsiDatasetFile(File file) {
		List<Tweet> tweets = null;
		String filename = file.getName();
		try {

			LOG.info("Reading Tweets from '" + filename + "' file.");
			tweets = new ArrayList<>();

			xssfWorkbook = new XSSFWorkbook(new FileInputStream(file));
			Sheet datatypeSheet = xssfWorkbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			//Pula o cabeçalho
			//Id|Date|Site Name|Title|URL|Sentiment|Country|State|City|Language|Synthesio Rank|Topics/Subtopics|Author name|Subscribers
			iterator.next();
			TweetDAO dao = new TweetDAO();
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();

				if(currentRow != null & currentRow.toString() != null){
					Cell cellId = currentRow.getCell(0);
					Cell cellDate = currentRow.getCell(1);
					Cell cellText = currentRow.getCell(3);
					Cell cellAuthorName = currentRow.getCell(12);

					if(cellId != null && cellId.toString() != null){
						String text = cellText.toString();
						boolean isRetweet = (text  != null) && (text.contains("RT"));
						Long id = Long.valueOf(cellId.toString().split("223494-")[1]);

						Date creationDate = getFormattedDate(cellDate.toString());
						Long userId = (long) generateUserId(cellAuthorName);
						Tweet t = new Tweet(id, userId, text, creationDate, isRetweet);

						dao.insert(t);
						tweets.add(t);
					}
				}
			}
		} catch (FileNotFoundException e) {
			LOG.error("File '" + filename + "' not found");
			e.printStackTrace();
		} catch (IOException e) {
			LOG.error("Unknown Exception.");
			e.printStackTrace();
		}
		return tweets;

	}

	private static Date getFormattedDate(String string) {
		//2017-02-28 21:46:23 +0100 CET
		//TODO como tratar esse fuso horário CET?
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse(string.substring(0,19));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return d;
	}

	private static int generateUserId(Cell cellAuthorName) {
		String userName = "";

		if(cellAuthorName != null){
			userName = cellAuthorName.toString();
		}

		return Math.abs(userName.hashCode());
	}

	private static String normalize(String text) {
		//TODO incluir mais caracteres?
		return text.replace("|", ";").trim();
	}
}
