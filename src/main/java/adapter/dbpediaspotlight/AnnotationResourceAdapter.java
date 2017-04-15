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

        JsonObject obj = json.getAsJsonObject();
        Map<String, Set<String>> types = new HashMap<>();

        for (String typ : obj.get("@types").getAsString().split(",")) {
            String[] keyValue = typ.split(":");
            String key = keyValue[0];
            String value = keyValue[1];
            if (types.containsKey(key))
                types.get(key).add(value);
            else {
                types.put(key, new HashSet<String>() {{
                    add(value);
                }});
            }
        }

        String uri = obj.get("@URI").getAsString();
        int support = obj.get("@support").getAsInt();
        String surfaceForm = obj.get("@surfaceForm").getAsString();
        int offset = obj.get("@offset").getAsInt();
        double similarityScore = obj.get("@similarityScore").getAsDouble();
        double percentageOfSecondRank = obj.get("@percentageOfSecondRank").getAsDouble();

        return new AnnotationResource(uri, support, types, surfaceForm,
            offset, similarityScore, percentageOfSecondRank);
    }
}
