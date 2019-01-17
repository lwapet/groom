package fr.groom.utils.logs.models;

import com.google.gson.*;
import fr.groom.utils.logs.HookConstant;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Log implements ILog {
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	static Pattern curlyBraceRegex = Pattern.compile("\\{(.*?)\\}");
	private static final String ANDROID_LOG_TIME_FORMAT = "MM-dd kk:mm:ss.SSS";
	private static SimpleDateFormat logCatDate = new SimpleDateFormat(ANDROID_LOG_TIME_FORMAT);
	String originalLine;
	private Timestamp timestamp;
	String rawData;
	JsonObject data;
	private UUID uuid;
	String method;
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
		JsonParser parser = new JsonParser();
		Pattern p = Pattern.compile("#(.*)#");
		Matcher m = p.matcher(this.originalLine);
		if (m.find()) {
			this.applicationPackageName = m.group(1);
		}
		this.data = (JsonObject) parser.parse(this.rawData);
		System.out.println(this.data.toString());
		/*
		for (JsonElement argO : this.logData.getAsJsonArray("arguments")) {
			JsonObject argument = argO.getAsJsonObject();
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
					System.err.println(data.get("method_signature").getAsString());
					System.err.println(argument.get("value").getAsString());
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
		this.uuid = UUID.fromString(data.get("monitoring_id").getAsString());
//		this.timestamp = parseLogTime(this.originalLine);
*/
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

	@Override
	public JsonObject getData() {
		return this.data;
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

