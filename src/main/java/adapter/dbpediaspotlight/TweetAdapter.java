package adapter.dbpediaspotlight;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import pojo.Tweet;

public class TweetAdapter implements JsonDeserializer<Tweet> {

	@Override
	public Tweet deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject obj = json.getAsJsonObject();

		final long id = obj.get("id").getAsLong();
		final long userId = obj.get("id").getAsLong();
		final String text = obj.get("text").getAsString();
		final String creationDateTxt = obj.get("createdat").getAsString();
		final boolean isRetweet = obj.get("isretweet").getAsBoolean();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		//sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date creationDate = null;
		try {
			creationDate = sdf.parse(creationDateTxt);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new Tweet(id, userId, text, creationDate, isRetweet);
	}
}
