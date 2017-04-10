package adapter.dbpediaspotlight;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import pojo.dbpediaspotlight.Annotation;
import pojo.dbpediaspotlight.AnnotationResource;

import java.lang.reflect.Type;
import java.util.List;

public class AnnotationAdapter implements JsonDeserializer<Annotation> {

    @Override
    public Annotation deserialize(JsonElement json, Type type, JsonDeserializationContext context)
        throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        Type listType = new TypeToken<List<AnnotationResource>>() {}.getType();
        List<AnnotationResource> resources = new Gson().fromJson(obj.get("Resources"), listType);

        String text = obj.get("@text").getAsString();
        float confidence = obj.get("@confidence").getAsFloat();
        int support = obj.get("@support").getAsInt();
        String types = obj.get("@types").getAsString();
        String sparql = obj.get("@sparql").getAsString();
        String policy = obj.get("@policy").getAsString();
        return new Annotation(text, confidence, support, types, sparql, policy, resources);
    }
}
