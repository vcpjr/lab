package adapter.dbpediaspotlight;

import adapter.dbpediaspotlight.AnnotationResourceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pojo.dbpediaspotlight.AnnotationResource;

import java.util.*;

public class AnnotationResourceAdapterTest {

    private static String uri;
    private static double similarityScore;
    private static double percentageOfSecondRank;
    private static AnnotationResource resource;

    @BeforeClass
    public static void setUpClass() {
        uri = "http://dbpedia.org/resource/Dell";
        similarityScore = 0.9999999862765493;
        percentageOfSecondRank = 8.873429642287647E-9;
        String json = String.format(Locale.US, "{"
            + "'@URI': '%s',"
            + "'@support': '1943',"
            + "'@types': 'DBpedia:Agent,Schema:Organization,DBpedia:Organisation,DBpedia:Company',"
            + "'@surfaceForm': 'Dell',"
            + "'@offset': '0',"
            + "'@similarityScore': '%.16f',"
            + "'@percentageOfSecondRank': '%.24f'"
            + "}", uri, similarityScore, percentageOfSecondRank);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(AnnotationResource.class, new AnnotationResourceAdapter())
            .create();
        resource = gson.fromJson(json, AnnotationResource.class);
    }

    @Test
    public void testDeserializedUri() {
        Assert.assertEquals(uri, resource.getURI());
    }

    @Test
    public void testDeserializedTypes() {
        Map<String, Set<String>> expectedTypes = new HashMap<String, Set<String>>() {{
            put("DBpedia", new HashSet<String>() {{
                add("Agent");
                add("Organisation");
                add("Company");
            }});

            put("Schema", new HashSet<String>() {{
                add("Organization");
            }});
        }};

        Assert.assertEquals(expectedTypes, resource.getTypes());
    }

    @Test
    public void testDeserializedSimilarityScore() {
        double delta = 1.0E-17;
        Assert.assertEquals(similarityScore, resource.getSimilarityScore(), delta);
    }

    @Test
    public void testDeserializedUriPercentageOfSecondRank() {
        double delta = 1.0E-24;
        Assert.assertEquals(percentageOfSecondRank, resource.getPercentageOfSecondRank(), delta);
    }
}
