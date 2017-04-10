package pojo.dbpediaspotlight;

import com.google.gson.annotations.SerializedName;

/**
 * Object class representing annotation resources
 * returned from DBPediaSpotlight requests
 */
public class AnnotationResource {

	@SerializedName("@URI")
	private String uri;
	@SerializedName("@support")
	private int support;
	@SerializedName("@types")
	private String types;
	@SerializedName("@surfaceForm")
	private String surfaceForm;
	@SerializedName("@offset")
	private int offset;
	@SerializedName("@similarityScore")
	private double similarityScore;
	@SerializedName("@percentageOfSecondRank")
	private double percentageOfSecondRank;

	public final String getURI() {
		return uri;
	}

	public final int getSupport() {
		return support;
	}

	public final String getTypes() {
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