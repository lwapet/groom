package fr.groom.logs.models;

import com.google.gson.*;
import fr.groom.logs.HookConstant;
import models.Switch;
import models.Switchable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Log implements ILog {
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static Pattern curlyBraceRegex = Pattern.compile("\\{(.*?)\\}");
	private static final String ANDROID_LOG_TIME_FORMAT = "MM-dd kk:mm:ss.SSS";
	private static SimpleDateFormat logCatDate = new SimpleDateFormat(ANDROID_LOG_TIME_FORMAT);
	JsonObject logData;
	private Timestamp timestamp;
	String method;
	String originalLine;
	String rawData;
	String applicationPackageName;
	ArrayList<StackTraceData> stackTrace = new ArrayList<>();
	Intent intent;
	int hashCode;

	public Log(String originalLine) {
		this.originalLine = originalLine;
		String encodedData = getStringInBetween(HookConstant.DATA_MARKER.getValue(), HookConstant.DATA_MARKER.getValue(), originalLine);
		byte[] decoded = Base64.getMimeDecoder().decode(encodedData);
		String data = decompress(decoded);
		this.rawData = data;
	}

	@Override
	public void parse() {
		Pattern p = Pattern.compile("#(.*)#");
		Matcher m = p.matcher(this.originalLine);
		if (m.find()) {
			JsonParser parser = new JsonParser();
			this.applicationPackageName = m.group(1);
			JsonObject data = (JsonObject) parser.parse(this.rawData);
			System.out.println(gson.toJson(data));
		}
	}

	//	@Override
	public void parse2() {
		JsonParser parser = new JsonParser();
		Pattern p = Pattern.compile("#(.*)#");
		Matcher m = p.matcher(this.originalLine);
		if (m.find()) {
			this.applicationPackageName = m.group(1);
			System.out.println(this.applicationPackageName);
		}
		JsonObject data = (JsonObject) parser.parse(this.rawData);
		this.logData = data;
		for (JsonElement argO : this.logData.getAsJsonArray("arguments")) {
			JsonObject argument = argO.getAsJsonObject();
			System.out.println(argument);
			JsonElement type = argument.get("type");
			if (!type.isJsonNull()) {
				if (type.getAsString().equals("android.view.WindowManager.LayoutParams")) {
					ArrayList<String> flags = new ArrayList<>();
					JsonObject value = argument.get("value").getAsJsonObject();
					int flagsValue = value.get("flags").getAsInt();
					Arrays.stream(LayoutParamsFlags.values()).forEach(flag -> {
						NumberFormat nf = NumberFormat.getInstance();
						try {
							Number n = nf.parse(flag.getHexString().replace("0x", ""));
							if ((n.intValue() & flagsValue) != 0) {
								flags.add(flag.name());
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
//						long constantFlagValue = Integer.decode(flag.getHexString());
					});
				} else if (type.getAsString().equals("android.widget.RelativeLayout")) {
					System.out.println(data.get("method_signature").getAsString());
					System.out.println(argument.get("value").getAsString());
				}
			}
		}
		if (data.has("intent")) {
			this.intent = new Intent(data.getAsJsonObject("intent"));
		}
		if (data.has("hash_code")) {
			this.hashCode = data.get("hash_code").getAsInt();
		}
		this.method = data.get("method_signature").getAsString();
		JsonArray elementArray = data.getAsJsonArray("stack_trace");
		for (int i = 0; i < elementArray.size(); i++) {
			JsonArray a = elementArray.get(i).getAsJsonArray();
			String className = a.get(0).getAsString();
			String methodName = a.get(1).getAsString();
			int lineNumber = a.get(2).getAsInt();
			StackTraceData stackTraceElement = new StackTraceData(className, methodName, lineNumber);
			stackTrace.add(stackTraceElement);
		}
//		this.timestamp = parseLogTime(this.originalLine);
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("type", this.getClass().getSimpleName());
		object.addProperty("timestamp", timestamp.toString());
		return object;
	}

	private static Timestamp parseLogTime(String line) {
		try {
			long logTimestamp = logCatDate.parse(line).getTime();
			return new Timestamp(logTimestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getOriginalLine() {
		return originalLine;
	}

	@Override
	public String getRawData() {
		return null;
	}


	public static String getStringInBetween(String pattern1, String pattern2, String string) {
		String regexString = Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2);
		Pattern pattern = Pattern.compile(regexString);
		// text contains the full text that you want to extract data

		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String decompress(byte[] bytes) {
		GZIPInputStream gis = null;
		String outStr = "";
		try {
			gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
			BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			String line;
			while ((line = bf.readLine()) != null) {
				outStr += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outStr;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public class StackTraceData {
		private String className;
		private String methodName;
		private int lineNumber;

		public StackTraceData(String className, String methodName, int lineNumber) {
			this.className = className;
			this.methodName = methodName;
			this.lineNumber = lineNumber;
		}

		public String getClassName() {
			return className;
		}

		public String getMethodName() {
			return methodName;
		}

		public int getLineNumber() {
			return lineNumber;
		}


		public JsonObject toJson() {
			 JsonObject data = new JsonObject();
			data.addProperty("class_name", className);
			data.addProperty("method_name", methodName);
			data.addProperty("line_number", lineNumber);
			return data;
		}

		@Override
		public String toString() {
			return "StackTraceData{" +
					"className='" + className + '\'' +
					", methodName='" + methodName + '\'' +
					", lineNumber=" + lineNumber +
					'}';
		}
	}
}

