package fr.groom.logs.models;

import fr.groom.logs.HookConstant;
import models.Switch;
import models.Switchable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	static Pattern curlyBraceRegex = Pattern.compile("\\{(.*?)\\}");
	private static final String ANDROID_LOG_TIME_FORMAT = "MM-dd kk:mm:ss.SSS";
	private static SimpleDateFormat logCatDate = new SimpleDateFormat(ANDROID_LOG_TIME_FORMAT);
	private Timestamp timestamp;
	String method;
	String originalLine;
	String rawData;
	String applicationPackageName;
	JSONObject logData;
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
			this.applicationPackageName = m.group(1);
			System.out.println(this.applicationPackageName);
		}
		JSONObject data = new JSONObject(this.rawData);
		this.logData = data;
		for (Object argO : this.logData.getJSONArray("arguments")) {
			JSONObject argument = null;
			try {
				argument = (JSONObject) argO;
			} catch (JSONException e) {
				System.out.println(argO);
				e.printStackTrace();
			}
			if (!argument.isNull("type")) {
				if (argument.getString("type").equals("android.view.WindowManager.LayoutParams")) {
					ArrayList<String> flags = new ArrayList<>();
					JSONObject value = argument.getJSONObject("value");
					int flagsValue = value.getInt("flags");
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
				} else if (argument.getString("type").equals("android.widget.RelativeLayout")) {
					System.out.println(data.getString("method_signature"));
					System.out.println(argument.getString("value"));
				}
			}
		}
		if (data.has("intent")) {
			this.intent = new Intent(data.getJSONObject("intent"));
		}
		if (data.has("hash_code")) {
			this.hashCode = data.getInt("hash_code");
		}
		this.method = data.getString("method_signature");
		JSONArray elementArray = data.getJSONArray("stack_trace");
		for (int i = 0; i < elementArray.length(); i++) {
			JSONArray a = elementArray.getJSONArray(i);
			String className = a.getString(0);
			String methodName = a.getString(1);
			int lineNumber = a.getInt(2);
			StackTraceData stackTraceElement = new StackTraceData(className, methodName, lineNumber);
			stackTrace.add(stackTraceElement);
		}
//		this.timestamp = parseLogTime(this.originalLine);
	}

	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("type", this.getClass().getSimpleName());
		object.put("timestamp", timestamp.toString());
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


		public JSONObject toJson() {
			JSONObject data = new JSONObject();
			data.put("class_name", className);
			data.put("method_name", methodName);
			data.put("line_number", lineNumber);
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

