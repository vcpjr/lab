package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import adapter.dbpediaspotlight.AnnotationAdapter;
import adapter.dbpediaspotlight.AnnotationResourceAdapter;
import adapter.dbpediaspotlight.TweetAdapter;
import pojo.Tweet;
import pojo.dbpediaspotlight.Annotation;
import pojo.dbpediaspotlight.AnnotationResource;

public class JsonConverter {
	
    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(AnnotationResource.class, new AnnotationResourceAdapter())
        .registerTypeAdapter(Annotation.class, new AnnotationAdapter())
        .registerTypeAdapter(Tweet.class, new TweetAdapter())
        .create();

    public static AnnotationResource toAnnotationResources(String json) {
        return gson.fromJson(json, AnnotationResource.class);
    }

    public static Annotation toAnnotation(String json) {
        return gson.fromJson(json, Annotation.class);
    }
    
    public static Tweet toTweet(String json) {
        return gson.fromJson(json, Tweet.class);
    }
    
}
