package pojo.dbpediaspotlight;

import java.util.Set;

import com.google.gson.annotations.SerializedName;

/**
 * Object class representing annotation resources
 * returned from DBPediaSpotlight requests
 * 
 * @author Willian Santos de Souza
 */
public class AnnotationResource {

    @SerializedName("@URI")
    private String uri;
    @SerializedName("@support")
    private int support;
    @SerializedName("@types")
    private Set<String> types;
    @SerializedName("@surfaceForm")
    private String surfaceForm;
    @SerializedName("@offset")
    private int offset;
    @SerializedName("@similarityScore")
    private double similarityScore;
    @SerializedName("@percentageOfSecondRank")
    private double percentageOfSecondRank;

    public AnnotationResource(String uri, int support, Set<String> types, String surfaceForm,
                              int offset, double similarityScore, double percentageOfSecondRank) {
        this.uri = uri;
        this.support = support;
        this.types = types;
        this.surfaceForm = surfaceForm;
        this.offset = offset;
        this.similarityScore = similarityScore;
        this.percentageOfSecondRank = percentageOfSecondRank;
    }

    public final String getURI() {
        return uri;
    }

    public final int getSupport() {
        return support;
    }

    public final Set<String> getTypes() {
        return types;
    }

    public final String getSurfaceForm() {
        return surfaceForm;
    }

    public final int getOffset() {
        return offset;
    }

    public final double getSimilarityScore() {
        return similarityScore;
    }

    public final double getPercentageOfSecondRank() {
        return percentageOfSecondRank;
    }
}