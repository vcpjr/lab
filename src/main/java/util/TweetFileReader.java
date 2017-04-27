package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Tweet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robinho on 17/04/17.
 */
public class TweetFileReader {
    private static final Logger LOG = LoggerFactory.getLogger(TweetFileReader.class);

    public static List<Tweet> readTweetsFromFile(File file) {
        List<Tweet> tweets = null;
        FileReader fr;
        String filename = file.getName();
        try {
            LOG.info("Reading tweets from '" + filename + "' file.");
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            tweets = new ArrayList<>();
            while (br.ready()) {
                tweets.add(new Tweet(br.readLine().trim()));
            }
        } catch (FileNotFoundException e) {
            LOG.error("File '" + filename + "' not found");
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error("Unknown Exception.");
            e.printStackTrace();
        }

        LOG.info("Read file.");
        return tweets;
    }
}
