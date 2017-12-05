package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adapter.dbpediaspotlight.TweetAdapter;
import pojo.Tweet;

public class TweetFileReader {
    private static final Logger LOG = LoggerFactory.getLogger(TweetFileReader.class);

    public static List<Tweet> readTweetsFromFile(File file) {
        List<Tweet> tweets = null;
        String filename = file.getName();
        try {
            LOG.info("Reading tweets from '" + filename + "' file.");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            tweets = new ArrayList<>();
            while (br.ready()) {
            	//TODO alterar aqui para pegar os demais campos do tweet (lendo do JSON)
				tweets.add(JsonConverter.toTweet(normalize(br.readLine())));
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

    private static String normalize(String text) {
    	//TODO incluir mais caracteres?
        return text.replace("|", ";").trim();
    }
}
