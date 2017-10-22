package service;

import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Tweet;
import pojo.dbpediaspotlight.Annotation;
import util.TweetFileReader;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class Reporter {

    public static final String APP_ROOT = System.getProperty("user.dir");
    private static final Logger LOG = LoggerFactory.getLogger(Reporter.class);

    private final float confidence;
    private final String language;
    private final List<Tweet> tweets;
    private final SpotlightRest rest;
    private SpotlightReport report;

    public Reporter(float confidence, String language, File datasetFile) {
        this.confidence = confidence;
        this.language = language;
        rest = new SpotlightRest();
        report = new SpotlightReport(APP_ROOT + "/output");
        tweets = TweetFileReader.readTweetsFromFile(datasetFile);
    }

    public void execute() {
        tweets.forEach((Tweet tweet) -> {
            try {
                final Instant start = Instant.now();
                final Annotation annotation = rest.getAnnotation(tweet.getMessage(), confidence, language);
                final Instant end = Instant.now();
                final long duration = Duration.between(start, end).toMillis();
                report.append(annotation, duration);
            } catch (UnexpectedStatusCodeException e) {
                LOG.error(String.format(Locale.US,
                    "Unexpected status code error: tweet=%s, confidence=%.2f, language=%s",
                    tweet.getMessage(), confidence, language),
                    e);
            } catch (JsonSyntaxException e) {
                LOG.error("Json Syntax error to annotate tweet message: '" + tweet.getMessage() + "'", e);
            } catch (Exception e) {
                LOG.error("Unknown exception", e);
            }
        });
        report.reportAnnotedClasses(String.format(Locale.US, "annoted-classes-%s-%s-%.2f.csv",
            LocalDate.now().toString(),
            language,
            confidence));

        report.reportAnnotedResources(String.format(Locale.US, "annoted-resources-%s-%s-%.2f.csv",
            LocalDate.now().toString(),
            language,
            confidence));
    }
}
