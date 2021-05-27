package fr.groom;

import org.json.JSONObject;

public interface Storage {
	public void insertData(JSONObject analysisData, String collectionName);

	public void update(JSONObject conditions, JSONObject update, String collectionName);

//	public void replace(JSONObject conditions, JSONObject update, String collectionName);
}
