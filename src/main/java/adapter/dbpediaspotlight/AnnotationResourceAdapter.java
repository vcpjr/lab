package adapter.dbpediaspotlight;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import pojo.dbpediaspotlight.AnnotationResource;

/**
 * Converts a JSON from Annotation, getting the annotated types from a resource.
 *  
 * Example:
 * {@code
 *      {
 * 			"@URI": "http://pt.dbpedia.org/resource/Twitter",
 *			"@support": "3432",
 *		    "@types": "Wikidata:Q386724,Schema:WebPage,Schema:CreativeWork,DBpedia:Work,DBpedia:Website",
 *			"@surfaceForm": "Twitter",
 *			"@offset": "50",
 *			"@similarityScore": "0.999999131483755",
 *			"@percentageOfSecondRank": "0.0"
 *		}
 *	}
 * 
 * Resultant AnnotationResource is the DBpedia LOD Resource "Twitter" containing 5 types (Wikidata:Q386724,Schema:WebPage,
 * Schema:CreativeWork,DBpedia:Work,DBpedia:Website)
 * 
 * @author Wilian Santos de Souza
 *
 */
public class AnnotationResourceAdapter implements JsonDeserializer<AnnotationResource> {

    @Override
    public AnnotationResource deserialize(JsonElement json, Type type, JsonDeserializationContext context)
        throws JsonParseException {

        final JsonObject obj = json.getAsJsonObject();
        final Set<String> types = !obj.get("@types").getAsString().isEmpty()
            ? new HashSet<>(Arrays.asList(obj.get("@types").getAsString().split(",")))
            : Collections.emptySet();
        final String uri = obj.get("@URI").getAsString();
        final int support = obj.get("@support").getAsInt();
        final String surfaceForm = obj.get("@surfaceForm").getAsString();
        final int offset = obj.get("@offset").getAsInt();
        final double similarityScore = obj.get("@similarityScore").getAsDouble();
        final double percentageOfSecondRank = obj.get("@percentageOfSecondRank").getAsDouble();

        return new AnnotationResource(uri, support, types, surfaceForm,
            offset, similarityScore, percentageOfSecondRank);
    }
}
