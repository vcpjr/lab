package adapter.dbpediaspotlight;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pojo.dbpediaspotlight.Annotation;
import pojo.dbpediaspotlight.AnnotationResource;

import java.util.List;
import java.util.Locale;

public class AnnotationAdapterTest {

    private static String expectedText;
    private static String expectedUri;
    private static float expectedConfidence;
    private static Annotation annotation;

    @BeforeClass
    public static void setUpClass() {
        expectedText = "Dell XPS 13 13.3\" QHD+ IPS Touchscreen Notebook Core i5 8GB Ram 256GB SSD 2.3GHz";
        expectedConfidence = 0.05f;
        expectedUri = "http://dbpedia.org/resource/Open_XML_Paper_Specification";
        String json = String.format(Locale.US, "{"
            + "  '@text': '%s',"
            + "  '@confidence': '%.2f',"
            + "  '@support': '0',"
            + "  '@types': '',"
            + "  '@sparql': '',"
            + "  '@policy': 'whitelist',"
            + "  'Resources': [{"
            + "      '@URI': 'http://dbpedia.org/resource/Dell',"
            + "      '@support': '1943',"
            + "      '@types': 'DBpedia:Agent,Schema:Organization,DBpedia:Organisation,DBpedia:Company',"
            + "      '@surfaceForm': 'Dell',"
            + "      '@offset': '0',"
            + "      '@similarityScore': '0.9999999862765493',"
            + "      '@percentageOfSecondRank': '8.873429642287647E-9'"
            + "  },"
            + "  {"
            + "    '@URI': '%s',"
            + "    '@support': '96',"
            + "    '@types': '',"
            + "    '@surfaceForm': 'XPS',"
            + "    '@offset': '5',"
            + "    '@similarityScore': '0.6126686296499774',"
            + "    '@percentageOfSecondRank': '0.27206459473073286'"
            + "  }]"
            + "}", expectedText, expectedConfidence, expectedUri);

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(Annotation.class, new AnnotationAdapter())
            .create();
        annotation = gson.fromJson(json, Annotation.class);
    }

    @Test
    public void testDeserializeText() {
        Assert.assertEquals(expectedText, annotation.getText());
    }

    @Test
    public void testDeserializeConfidence() {
        float delta = 0.001f;
        Assert.assertEquals(expectedConfidence, annotation.getConfidence(), delta);
    }
    @Test
    public void testDeserializeResources() {
        List<AnnotationResource> resources = annotation.getResources();
        Assert.assertEquals(2, resources.size());
        Assert.assertTrue(resources.get(1).getTypes().isEmpty());
        Assert.assertEquals(expectedUri, resources.get(1).getURI());
    }
}
