package fr.groom.logs.models;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Intent {
	private static final String ACTION_KEY = "act";
	private static final String COMPONENT_KEY = "cmp";
	private static final String CATEGORY_KEY = "cat";
	private static final String FLAG_KEY = "flg";
	private static final String BOUNDS_KEY = "bnds";
	private static List<Pair<String, Pattern>> REGEX_PATTERNS = new ArrayList<>();

	static {
		REGEX_PATTERNS.add(new Pair<>(ACTION_KEY, Pattern.compile(ACTION_KEY + "=(.*?) ")));
		REGEX_PATTERNS.add(new Pair<>(COMPONENT_KEY, Pattern.compile(COMPONENT_KEY + "=(.*?) ")));
		REGEX_PATTERNS.add(new Pair<>(CATEGORY_KEY, Pattern.compile(CATEGORY_KEY + "=(.*?) ")));
		REGEX_PATTERNS.add(new Pair<>(FLAG_KEY, Pattern.compile(FLAG_KEY + "=(.*?) ")));
		REGEX_PATTERNS.add(new Pair<>(BOUNDS_KEY, Pattern.compile(BOUNDS_KEY + "=(.*?) ")));
	}

	private String action;
	private String component;
	private String category;
	private String packageName;
	private String type;
	private String data;
	private String scheme;
	private Intent selector;
	private int flagsValue;
	private ArrayList<String> flags = new ArrayList<>();
	private String boundsValue;
	private UUID uuid;
	private ArrayList<String> extras = new ArrayList<>();

	Intent(JSONObject jsonIntent) {
		if (jsonIntent.has("action"))
			this.action = jsonIntent.getString("action");
		if (jsonIntent.has("component"))
			this.component = jsonIntent.getString("component");
		if (jsonIntent.has("categories"))
			this.category = jsonIntent.get("categories").toString();
		if (jsonIntent.has("package"))
			this.packageName = jsonIntent.get("package").toString();
		if(jsonIntent.has("type"))
			this.type = jsonIntent.get("type").toString();
		if(jsonIntent.has("data"))
			this.data = jsonIntent.get("data").toString();
		if(jsonIntent.has("scheme"))
			this.scheme = jsonIntent.get("scheme").toString();
		if(jsonIntent.has("selector"))
			this.selector = new Intent(jsonIntent.getJSONObject("selector"));

		if (jsonIntent.has("flags")) {
			flagsValue = jsonIntent.getInt("flags");
			Arrays.stream(IntentFlags.values()).forEach(flag -> {
				long constantFlagValue = Integer.decode(flag.getHexString());
				if ((constantFlagValue & flagsValue) != 0) {
					flags.add(flag.name());
				}
			});
		}
		if (jsonIntent.has("extras")) {
			JSONArray extras = jsonIntent.getJSONArray("extras");
			Iterator<Object> i = extras.iterator();
			this.extras = new ArrayList<>();
			while (i.hasNext()) {
				Object extraObject = i.next();
				JSONObject extra = (JSONObject) extraObject;
				if (extra.has("UUID")) {
					this.uuid = UUID.fromString(extra.getString("UUID"));
				} else {
					this.extras.add(extra.toString());
				}
			}
		}
	}

	Intent(String intentString) {
		REGEX_PATTERNS.forEach(pattern -> {
			Matcher matcher = pattern.getValue().matcher(intentString);
			if (matcher.find()) {
				storeValue(pattern.getKey(), matcher.group(1));
			}
		});
	}

	private void storeValue(String key, String value) {
		switch (key) {
			case ACTION_KEY:
				action = value;
				break;
			case COMPONENT_KEY:
				component = value;
				break;
			case CATEGORY_KEY:
				category = value;
				break;
			case FLAG_KEY:
				flagsValue = Integer.decode(value);
				Arrays.stream(IntentFlags.values()).forEach(flag -> {
					long constantFlagValue = Integer.decode(flag.getHexString());
					if ((constantFlagValue & flagsValue) != 0) {
						flags.add(flag.name());
					}
				});
				break;
			case BOUNDS_KEY:
				boundsValue = value;
				break;
			default:
				try {
					throw new Exception("Unknown intent key");
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public String toString() {
		return "Intent{" +
				"\naction='" + action + '\'' +
				"\ncomponent='" + component + '\'' +
				"\ncategory='" + category + '\'' +
				"\npackageName='" + packageName + '\'' +
				"\ntype='" + type + '\'' +
				"\ndata='" + data + '\'' +
				"\nscheme='" + scheme + '\'' +
				"\nselector=" + selector +
				"\nflagsValue=" + flagsValue +
				"\nflags=" + flags +
				"\nboundsValue='" + boundsValue + '\'' +
				"\nuuid=" + uuid +
				"\nextras=" + extras +
				'}';
	}

	JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("action", this.action);
		object.put("component", this.component);
		object.put("category", this.category);
		object.put("flagsValue", this.flagsValue);
		object.put("flags", this.flags.toString());
		object.put("boundsValue", this.boundsValue);
		object.put("hasExtras", !this.extras.isEmpty());
		return object;
	}

	public String getComponent() {
		if (component == null) {
			return null;
		}
		return component;
	}

	public ArrayList<String> getFlags() {
		return flags;
	}

	public String getCategory() {
		return category;
	}

	public String getAction() {
		return action;
	}

	public ArrayList<String> getExtras() {
		return extras;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	public String getScheme() {
		return scheme;
	}

	public Intent getSelector() {
		return selector;
	}

	public int getFlagsValue() {
		return flagsValue;
	}

	public String getBoundsValue() {
		return boundsValue;
	}
}

