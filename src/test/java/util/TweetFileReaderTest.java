package util;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import pojo.Tweet;

public class TweetFileReaderTest {

    @Test
    public void testReadTweetsFromFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourceFile = new File(classLoader.getResource("test/DatasetTweets.txt").getFile());

        List<Tweet> tweets = TweetFileReader.readTweetsFromFile(resourceFile);
        Assert.assertEquals(8, tweets.size());
        tweets.forEach(tweet -> Assert.assertFalse(tweet.getText().contains("|")));
    }
}
