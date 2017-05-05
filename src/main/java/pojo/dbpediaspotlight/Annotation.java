package pojo.dbpediaspotlight;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Object class representing annotation
 * returned from DBPediaSpotlight requests
 */
public class Annotation {

    @SerializedName("@text")
    private String text;
    @SerializedName("@confidence")
    private float confidence;
    @SerializedName("@support")
    private int support;
    @SerializedName("@types")
    private String types;
    @SerializedName("@sparql")
    private String sparql;
    @SerializedName("@policy")
    private String policy;
    @SerializedName("Resources")
    private List<AnnotationResource> resources = null;

    public Annotation(String text, float confidence, int support, String types, String sparql,
                      String policy, List<AnnotationResource> resources) {
        this.text = text;
        this.confidence = confidence;
        this.support = support;
        this.types = types;
        this.sparql = sparql;
        this.policy = policy;
        this.resources = resources;
    }

    public final String getText() {
        return text;
    }

    public final float getConfidence() {
        return confidence;
    }

    public final int getSupport() {
        return support;
    }

    public final String getTypes() {
        return types;
    }

    public final String getSparql() {
        return sparql;
    }

    public final String getPolicy() {
        return policy;
    }

    public final List<AnnotationResource> getResources() {
        return resources;
    }

    public final int getResourcesSize() {
        return resources.size();
    }
}