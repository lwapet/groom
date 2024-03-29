package fr.groom.utils.logs.models;


import com.google.gson.JsonObject;

public interface ILog {
	public void parse();
	public JsonObject toJson();
	public String getOriginalLine();
	public String getRawData();
	public JsonObject getData();
}
