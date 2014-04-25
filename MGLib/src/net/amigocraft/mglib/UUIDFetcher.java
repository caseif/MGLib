package net.amigocraft.mglib;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for fetching UUIDs from Mojang's servers.
 * @author Gravity
 */
class UUIDFetcher {

	private static final int MAX_SEARCH = 100;
	private static final String PROFILE_URL = "https://api.mojang.com/profiles/page/";
	private static final String AGENT = "minecraft";
	private static final JSONParser jsonParser = new JSONParser();

	public static UUID getUUID(String name) throws Exception {
		String body = UUIDFetcher.buildBody(name);
		for (int i = 1; i < MAX_SEARCH; i++) {
			HttpURLConnection connection = createConnection(i);
			writeBody(connection, body);
			JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
			JSONArray array = (JSONArray) jsonObject.get("profiles");
			Number count = (Number) jsonObject.get("size");
			if (count.intValue() == 0) {
				break;
			}
			for (Object profile : array) {
				JSONObject jsonProfile = (JSONObject) profile;
				String id = (String) jsonProfile.get("id");
				return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" +id.substring(20, 32));
			}
		}
		return null;
	}

	private static void writeBody(HttpURLConnection connection, String body) throws Exception {
		DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
		writer.write(body.getBytes());
		writer.flush();
		writer.close();
	}

	private static HttpURLConnection createConnection(int page) throws Exception {
		URL url = new URL(PROFILE_URL+page);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}
	@SuppressWarnings("unchecked")
	private static String buildBody(String name) {
		List<JSONObject> lookups = new ArrayList<JSONObject>();
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("agent", AGENT);
		lookups.add(obj);
		return JSONValue.toJSONString(lookups);
	}
}