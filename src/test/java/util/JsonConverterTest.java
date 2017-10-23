package util;

import org.junit.Assert;
import org.junit.Test;
import pojo.dbpediaspotlight.Annotation;
import pojo.dbpediaspotlight.AnnotationResource;

import java.util.*;

public class JsonConverterTest {

    final double OFFSET = 0.001;

    boolean hasEquals(AnnotationResource lhs, AnnotationResource rhs) {
        return lhs.getURI().equals(rhs.getURI())
            && lhs.getSupport() == rhs.getSupport()
            && lhs.getTypes().equals(rhs.getTypes())
            && lhs.getSurfaceForm().equals(rhs.getSurfaceForm())
            && lhs.getOffset() == rhs.getOffset()
            && Math.abs(lhs.getSimilarityScore() - rhs.getSimilarityScore()) < OFFSET
            && Math.abs(lhs.getPercentageOfSecondRank() - rhs.getPercentageOfSecondRank()) < OFFSET;
    }

    boolean hasEquals(Annotation lhs, Annotation rhs) {
        List<AnnotationResource> lRes = lhs.getResources();
        List<AnnotationResource> rRes = rhs.getResources();
        lRes.sort(Comparator.comparing(AnnotationResource::getURI));
        rRes.sort(Comparator.comparing(AnnotationResource::getURI));

        if (lRes.size() != rRes.size()) {
            return false;
        }

        for (int i = 0; i < lhs.getResourcesSize(); ++i) {
            if (!hasEquals(lRes.get(i), rRes.get(i))) {
                return false;
            }
        }

        return lhs.getText().equals(rhs.getText())
            && Math.abs(lhs.getConfidence() - rhs.getConfidence()) <= OFFSET
            && lhs.getSupport() == rhs.getSupport()
            && lhs.getTypes().equals(rhs.getTypes())
            && lhs.getSparql().equals(rhs.getSparql())
            && lhs.getPolicy().equals(rhs.getPolicy());
    }

    @Test
    public void testConvertJsonToAnnotation() {
        final String json = "{" +
            "  '@text':'A textbook'," +
            "  '@confidence':'0.05'," +
            "  '@support':'0'," +
            "  '@types':''," +
            "  '@sparql':''," +
            "  '@policy':'whitelist'," +
            "  'Resources':[" +
            "    {" +
            "      '@URI':'http://dbpedia.org/resource/Business'," +
            "      '@support':'1943'," +
            "      '@types':'DBpedia:Organisation,DBpedia:Company'," +
            "      '@surfaceForm':'A Business'," +
            "      '@offset':'0'," +
            "      '@similarityScore':'0.0'," +
            "      '@percentageOfSecondRank':'4.0'" +
            "    }" +
            "  ]" +
            "}";

        Annotation ann = new Annotation(
            "A textbook", 0.05f, 0, "", "", "whitelist",
            Collections.singletonList(new AnnotationResource("http://dbpedia.org/resource/Business", 1943,
                new HashSet<>(Arrays.asList("DBpedia:Organisation", "DBpedia:Company")),
                "A Business", 0, 0, 4
            ))
        );
        Assert.assertTrue(hasEquals(JsonConverter.toAnnotation(json), ann));
    }
}
