package fr.groom.logs.models;

import org.json.JSONObject;

public interface ILog {
	public void parse();
	public JSONObject toJson();
	public String getOriginalLine();
	public String getRawData();
}
