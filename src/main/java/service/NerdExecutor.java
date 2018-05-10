package service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;

import pojo.Tweet;
import pojo.dbpediaspotlight.Annotation;
import util.CSVReport;
import util.TweetFileReader;

public class NerdExecutor {

    private static final String APP_ROOT = System.getProperty("user.dir");
    private static final Logger LOG = LoggerFactory.getLogger(NerdExecutor.class);
    private static final File outputPath = new File(APP_ROOT,"output");

    private final List<Tweet> tweets;
    private final SpotlightRest rest;
    private final String inputFilename;

    public NerdExecutor(File datasetFile) {
        rest = new SpotlightRest();
        tweets = TweetFileReader.readTweetsFromFile(datasetFile);

        String filePath = datasetFile.getName();
        inputFilename = filePath.split("\\.")[0];
    }

    public void execute(float confidence, String language) {
    	//TODO alterar para incluir: 
    	//tweet id (id), user id, text, pre-processed text, confidence, mention, URI, Chosen class, Request time
        CSVReport resourceReport = new CSVReport("Tweet id(#); User id; Text; Confidence; Resource URI");
        CSVReport classReport = new CSVReport("Tweet id(#); User id; Text; Confidence; Classes");

        //TODO confirmar a contagem de HITS (criar KGNode? Salvar no banco?)
        //Diretos: menções a instâncias
        //Indiretos por Type: uma classe diretamente relacionada à uma instância
        //Indiretos por Subclass-Of: uma classe é relacionada através de uma relação de subsumption até uma classe type de uma instância
        
        /*
         * Exemplo: 
         * 
         * Resource(directHits, indirectHitsType, indirectHitsSubclassOf)
         * 			
         *    	 Thing(0,0,2)
         *	       |
         *	     Person(0,0,2)
         *	       |
         *	     Singer(0,2,0)
         *    	/             \
         * Selena(1,0,0)	 Anitta(1,0,0)			   
         * 
         * */
        
        for(int i = 0; i < tweets.size(); ++i) {
            try {
            	Tweet t = tweets.get(i);
                final Annotation annotated = rest.getAnnotation(t.getText(), confidence, language);
                annotated.getResources()
                    .forEach(resource -> {
                        // tweet id(#);Resources
                        final String annotatedResource = String.format(Locale.US, "%s;%s;%s;%.5f;%s", t.getId(), t.getUserId(), t.getText(), confidence, resource.getURI());
                        resourceReport.append(annotatedResource);
                        resource.getTypes().forEach(classType-> {
                                // Tweet id(#);classes
                                final String resourceClass = String.format(Locale.US, "%s;%s;%s;%.5f;%s", t.getId(), t.getUserId(), t.getText(), confidence, classType);
                                classReport.append(resourceClass);
                            }
                        );
                    });
            } catch (UnexpectedStatusCodeException e) {
                LOG.error(String.format(Locale.US,
                    "Unexpected status code error: tweet=%s, confidence=%.2f, language=%s",
                    tweets.get(i).getText(), confidence, language),
                    e);
            } catch (JsonSyntaxException e) {
                LOG.error("Json Syntax error to annotate tweet message: '" + tweets.get(i).getText() + "'", e);
            } catch (Exception e) {
                LOG.error("Unknown exception", e);
            }
        }

        String postfixFilename = String.format(
            Locale.US,
            "%s-%s-%s-%.2f.csv",
            inputFilename.replaceAll("\\s", ""),
            LocalDate.now().toString(),
            language,
            confidence);

        resourceReport.generate(new File(outputPath, "resources-" + postfixFilename));
        classReport.generate(new File(outputPath, "classes-" + postfixFilename));
    }
}