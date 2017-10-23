package service;

import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Tweet;
import pojo.dbpediaspotlight.Annotation;
import util.CSVReport;
import util.TweetFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class NerdExecutor {

    private static final String APP_ROOT = System.getProperty("user.dir");
    private static final Logger LOG = LoggerFactory.getLogger(NerdExecutor.class);
    private static final File outputPath = new File(APP_ROOT,"output");

    private final List<Tweet> tweets;
    private final SpotlightRest rest;

    public NerdExecutor(File datasetFile) {
        rest = new SpotlightRest();
        tweets = TweetFileReader.readTweetsFromFile(datasetFile);
    }

    public void execute(float confidence, String language) {
        CSVReport resourceReport = new CSVReport("Tweet id(#);Resources");
        CSVReport classReport = new CSVReport("Tweet id(#);Classes");

        for(int i = 0; i < tweets.size(); ++i) {
            final int tweetId = i + 1;
            try {
                final Annotation annotated = rest.getAnnotation(tweets.get(i).getMessage(), confidence, language);
                annotated.getResources()
                    .forEach(resource -> {
                        // tweet id(#);Resources
                        final String annotatedResource = String.format(Locale.US, "%s;%s\n", tweetId, resource.getURI());
                        resourceReport.append(annotatedResource);
                        resource.getTypes().forEach(classType-> {
                                // Tweet id(#);classes
                                final String resourceClass = String.format(Locale.US, "%s;%s\n", tweetId, classType);
                                classReport.append(resourceClass);
                            }
                        );
                    });
            } catch (UnexpectedStatusCodeException e) {
                LOG.error(String.format(Locale.US,
                    "Unexpected status code error: tweet=%s, confidence=%.2f, language=%s",
                    tweets.get(i).getMessage(), confidence, language),
                    e);
            } catch (JsonSyntaxException e) {
                LOG.error("Json Syntax error to annotate tweet message: '" + tweets.get(i).getMessage() + "'", e);
            } catch (Exception e) {
                LOG.error("Unknown exception", e);
            }
        }

        String postfixFilename = String.format(
            Locale.US,
            "%s-%s-%.2f.csv",
            LocalDate.now().toString(),
            language,
            confidence);
;
        resourceReport.generate(new File(outputPath, "annotated-resources-" + postfixFilename));
        classReport.generate(new File(outputPath, "annotated-classes-" + postfixFilename));
    }
}
