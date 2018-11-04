package service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pojo.dbpediaspotlight.Annotation;
import util.JsonConverter;

/**
 * Call DBpedia Spotlight (local or remote) for annotate TCs (e.g., tweets).
 * 
 * DBpedia spotlight can be run locally on Docker: https://github.com/dbpedia-spotlight/spotlight-docker
 * 
 * @author Vilmar César Pereira Júnior
 * 	       Willian Santos de Souza
 *
 */
public class SpotlightRest {
    private static final Logger LOG = LoggerFactory.getLogger(SpotlightRest.class);
    
    //LOCAL SPOTLIGHT (DOCKER)
    private static String HOST = "localhost:2228/rest";

    //REMOTE SPOTLIGHT (DOCKER)
    //private static String HOST = "model.dbpedia-spotlight.org";
    
    private static String PROTOCOL = "http";
    private static String ANNOTATE_PATH = "/annotate";


    private Annotation httpHandle(HttpURLConnection conn) throws IOException, UnexpectedStatusCodeException {
        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOG.error("URL=" + conn.getURL()
                + ", status=" + conn.getResponseCode()
                + ", body=" + conn.getResponseMessage());
            throw new UnexpectedStatusCodeException("URL=" + conn.getURL()
                + ", status=" + conn.getResponseCode()
                + ", body=" + conn.getResponseMessage());
        }

        LOG.info("Spotlight request successful.");
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            builder.append(output);
        }

        br.close();
        String json = builder.toString();
        LOG.debug("Spotlight response: " + json);
        return JsonConverter.toAnnotation(json);
    }

    private void sendRequest(HttpURLConnection conn, String params) throws IOException {
    	DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
    	byte[] postData = params.getBytes( StandardCharsets.UTF_8 );
        writer.write(postData);
        writer.flush();
        writer.close();
    }

    Annotation getAnnotation(String text, float confidence, String language)
        throws UnexpectedStatusCodeException {

        String path = ANNOTATE_PATH.replace("{language}", language);
        HttpURLConnection conn = null;
        Annotation annotation = null;

        LOG.info(String.format(Locale.US,
            "Retrieving spotlight annotate with: confidence=%.2f, language=%s, text='%s'",
            confidence, language, text));

        try {
            URL url = new URL(PROTOCOL, HOST, path);
            url = new URL("http://localhost:2228/rest/annotate");
            //String s = JsonPath.parse(new URL("http://localhost:2228/rest/annotate")).jsonString();
            
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String params = String.format(Locale.US, "text=%s&confidence=%.2f",
                encodedText, confidence);

            sendRequest(conn, params);
            annotation = httpHandle(conn);

        } catch (Exception e) {
            LOG.error("Unexpected error.", e);
            e.printStackTrace();
        } finally {
            assert conn != null;
            conn.disconnect();
        }
        return annotation;
    }
}