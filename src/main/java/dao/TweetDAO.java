package dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import entity.Tweet;
import entity.filter.TweetFilter;
import main.Main;

public class TweetDAO {

	public Tweet getRandomTweet() {

		/**
		 * Select a random row with MySQL:
		 * 
		 * SELECT column FROM table ORDER BY RAND() LIMIT 1 Select a random row
		 * with PostgreSQL:
		 * 
		 * SELECT column FROM table ORDER BY RANDOM() LIMIT 1 Select a random
		 * row with Microsoft SQL Server:
		 * 
		 * SELECT TOP 1 column FROM table ORDER BY NEWID() Select a random row
		 * with IBM DB2
		 * 
		 * SELECT column, RAND() as IDX FROM table ORDER BY IDX FETCH FIRST 1
		 * ROWS ONLY Select a random record with Oracle:
		 * 
		 * SELECT column FROM ( SELECT column FROM table ORDER BY
		 * dbms_random.value ) WHERE rownum = 1
		 **/
		// TODO
		return null;
	}

	public Tweet getTweetByFilter(TweetFilter filter) {
		// TODO
		return null;
	}

	public ArrayList<Tweet> getRandomList(int size) {
		// TODO como usar o size para uma lista randomica?
		ArrayList<Tweet> tweets = new ArrayList<>();
		// TODO conectar aos datasets de tweets

		String path = Main.PATH_DROPBOX_TJ + "dataset_Fabio_Bif.txt";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();

				Tweet t = new Tweet();
				t.setMessage(line);

				tweets.add(t);
				// System.out.println(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tweets;
	}
}
