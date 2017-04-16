package util;

import adapter.dbpediaspotlight.AnnotationAdapter;
import adapter.dbpediaspotlight.AnnotationResourceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pojo.dbpediaspotlight.Annotation;
import pojo.dbpediaspotlight.AnnotationResource;

public class JsonConverter {
    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(AnnotationResource.class, new AnnotationResourceAdapter())
        .registerTypeAdapter(Annotation.class, new AnnotationAdapter())
        .create();

    public static AnnotationResource toAnnotationResources(String json) {
        return gson.fromJson(json, AnnotationResource.class);
    }

    public static Annotation toAnnotation(String json) {
        return gson.fromJson(json, Annotation.class);
    }
}
