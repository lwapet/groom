package fr.groom;

import org.json.JSONObject;

public class Printer implements Storage {
	@Override
	public void insertData(JSONObject analysisData, String collectionName) {
		System.out.println("==== " + collectionName + " insert start ====");
		System.out.println(analysisData.toString(4));
		System.out.println("==== " + collectionName + " insert end ====");
	}

	@Override
	public void update(JSONObject update, JSONObject conditions, String collectionName) {
		System.out.println("==== " + collectionName + " update start ====");
		System.out.println(conditions.toString(4));
		System.out.println(update.toString(4));
		System.out.println("==== " + collectionName + " update end ====");
	}

	@Override
	public void replace(JSONObject update, JSONObject conditions, String collectionName) {
		System.out.println("==== " + collectionName + " replace start ====");
		System.out.println(conditions.toString(4));
		System.out.println(update.toString(4));
		System.out.println("==== " + collectionName + " replace end ====");
	}
}
