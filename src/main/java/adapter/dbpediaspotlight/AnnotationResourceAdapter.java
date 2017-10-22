package adapter.dbpediaspotlight;

import com.google.gson.*;
import pojo.dbpediaspotlight.AnnotationResource;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
