package fr.groom;

import fr.groom.core.QuotedStringTokenizer;
import fr.groom.core.VariableResolver;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	private static final Pattern VARIABLE = Pattern.compile("\\$([A-Za-z0-9_]+|\\{[A-Za-z0-9_.]+\\}|\\$)");
	public static String fixNull(String s) {
		return s == null ? "" : s;
	}

	public static String fixEmpty(String s) {
		return s != null && s.length() != 0 ? s : null;
	}

	public static String fixEmptyAndTrim(String s) {
		return s == null ? null : fixEmpty(s.trim());
	}

	public static String[] tokenize(String s, String delimiter) {
		return QuotedStringTokenizer.tokenize(s, delimiter);
	}

	public static String[] tokenize(String s) {
		return tokenize(s, " \t\n\r\f");
	}

	public static String replaceMacro( String s,  Map<String, String> properties) {
		return replaceMacro(s, (VariableResolver)(new VariableResolver.ByMap(properties)));
	}

	public static Properties loadProperties(String properties) throws IOException {
		Properties p = new Properties();
		p.load(new StringReader(properties));
		return p;
	}

	public static String replaceMacro( String s,  VariableResolver<String> resolver) {
		if (s == null) {
			return null;
		} else {
			int idx = 0;

			while(true) {
				Matcher m = VARIABLE.matcher(s);
				if (!m.find(idx)) {
					return s;
				}

				String key = m.group().substring(1);
				String value;
				if (key.charAt(0) == '$') {
					value = "$";
				} else {
					if (key.charAt(0) == '{') {
						key = key.substring(1, key.length() - 1);
					}

					value = (String)resolver.resolve(key);
				}

				if (value == null) {
					idx = m.end();
				} else {
					s = s.substring(0, m.start()) + value + s.substring(m.end());
					idx = m.start() + value.length();
				}
			}
		}
	}

}
