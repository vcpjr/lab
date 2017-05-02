package service;

import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.dbpediaspotlight.Annotation;
import util.JsonConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class SpotlightRest {
    private static final Logger LOG = LoggerFactory.getLogger(SpotlightRest.class);

    private static String PROTOCOL = "http";
    private static String HOST = "model.dbpedia-spotlight.org";
    private static String ANNOTATE_PATH = "/{language}/annotate";

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
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(params);
        writer.flush();
        writer.close();
    }

    public Annotation getAnnotation(String text, float confidence, String language)
        throws UnexpectedStatusCodeException {

        String path = ANNOTATE_PATH.replace("{language}", language);
        HttpURLConnection conn = null;
        Annotation annotation = null;

        LOG.info(String.format(Locale.US,
            "Retrieving spotlight annotate with: confidence=%.2f, language=%s, text='%s'",
            confidence, language, text));

        try {
            URL url = new URL(PROTOCOL, HOST, path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(3000);
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String params = String.format(Locale.US, "text=%s&confidence=%.2f",
                encodedText, confidence);

            sendRequest(conn, params);
            annotation = httpHandle(conn);

        } catch (IOException e) {
            LOG.error("Unexpected error.", e);
            e.printStackTrace();
        } finally {
            assert conn != null;
            conn.disconnect();
        }
        return annotation;
    }
}
