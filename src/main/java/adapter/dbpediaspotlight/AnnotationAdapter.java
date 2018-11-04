package adapter.dbpediaspotlight;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import pojo.dbpediaspotlight.Annotation;
import pojo.dbpediaspotlight.AnnotationResource;

/**
 * Converts a JSON from a Annotation
 * 
 * @author Wilian Santos de Souza
 *
 */
public class AnnotationAdapter implements JsonDeserializer<Annotation> {

    @Override
    public Annotation deserialize(JsonElement json, Type type, JsonDeserializationContext context)
        throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();

        Gson gson = new GsonBuilder()
            .registerTypeAdapter(AnnotationResource.class, new AnnotationResourceAdapter())
            .create();

        List<AnnotationResource> resources = new ArrayList<>();
        if (obj.has("Resources")) {
            final JsonArray jsonResources = obj.getAsJsonArray("Resources");
            for (JsonElement element : jsonResources) {
                final AnnotationResource resource = gson.fromJson(element, AnnotationResource.class);
                resources.add(resource);
            }
        }

        final String text = obj.get("@text").getAsString();
        final float confidence = obj.get("@confidence").getAsFloat();
        final int support = obj.get("@support").getAsInt();
        final String types = obj.get("@types").getAsString();
        final String sparql = obj.get("@sparql").getAsString();
        final String policy = obj.get("@policy").getAsString();
        return new Annotation(text, confidence, support, types, sparql, policy, resources);
    }
}
