package dao;

import java.util.ArrayList;

import entity.Tweet;
import entity.filter.TweetFilter;

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

		ArrayList<Tweet> tweets = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			tweets.add(this.getRandomTweet());
		}

		return tweets;
	}
}
