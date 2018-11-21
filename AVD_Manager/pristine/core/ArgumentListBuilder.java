package fr.groom.core;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import fr.groom.Util;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ArgumentListBuilder implements Serializable, Cloneable {
	private final List<String> args = new ArrayList();
	private BitSet mask = new BitSet();
	private static final long serialVersionUID = 1L;

	public ArgumentListBuilder() {
	}

	public ArgumentListBuilder(String... args) {
		this.add(args);
	}

	public ArgumentListBuilder add(Object a) {
		return this.add(a.toString(), false);
	}

	public ArgumentListBuilder add(Object a, boolean mask) {
		return this.add(a.toString(), mask);
	}

	public ArgumentListBuilder add(File f) {
		return this.add(f.getAbsolutePath(), false);
	}

	public ArgumentListBuilder add(String a) {
		return this.add(a, false);
	}

	public ArgumentListBuilder add(String a, boolean mask) {
		if (a != null) {
			if (mask) {
				this.mask.set(this.args.size());
			}

			this.args.add(a);
		}

		return this;
	}

	public ArgumentListBuilder prepend(String... args) {
		BitSet nm = new BitSet(this.args.size() + args.length);

		for(int i = 0; i < this.args.size(); ++i) {
			nm.set(i + args.length, this.mask.get(i));
		}

		this.mask = nm;
		this.args.addAll(0, Arrays.asList(args));
		return this;
	}

	public ArgumentListBuilder addQuoted(String a) {
		return this.add('"' + a + '"', false);
	}

	public ArgumentListBuilder addQuoted(String a, boolean mask) {
		return this.add('"' + a + '"', mask);
	}

	public ArgumentListBuilder add(String... args) {
		String[] arr$ = args;
		int len$ = args.length;

		for(int i$ = 0; i$ < len$; ++i$) {
			String arg = arr$[i$];
			this.add(arg);
		}

		return this;
	}

	public ArgumentListBuilder addTokenized(String s) {
		if (s == null) {
			return this;
		} else {
			this.add(Util.tokenize(s));
			return this;
		}
	}

	public ArgumentListBuilder addKeyValuePair(String prefix, String key, String value, boolean mask) {
		if (key == null) {
			return this;
		} else {
			this.add((prefix == null ? "-D" : prefix) + key + '=' + value, mask);
			return this;
		}
	}

	public ArgumentListBuilder addKeyValuePairs(String prefix, Map<String, String> props) {
		Iterator i$ = props.entrySet().iterator();

		while(i$.hasNext()) {
			Entry<String, String> e = (Entry)i$.next();
			this.addKeyValuePair(prefix, (String)e.getKey(), (String)e.getValue(), false);
		}

		return this;
	}

	public ArgumentListBuilder addKeyValuePairs(String prefix, Map<String, String> props, Set<String> propsToMask) {
		Iterator i$ = props.entrySet().iterator();

		while(i$.hasNext()) {
			Entry<String, String> e = (Entry)i$.next();
			this.addKeyValuePair(prefix, (String)e.getKey(), (String)e.getValue(), propsToMask == null ? false : propsToMask.contains(e.getKey()));
		}

		return this;
	}

	public ArgumentListBuilder addKeyValuePairsFromPropertyString(String prefix, String properties, VariableResolver<String> vr) throws IOException {
		return this.addKeyValuePairsFromPropertyString(prefix, properties, vr, (Set)null);
	}

	public ArgumentListBuilder addKeyValuePairsFromPropertyString(String prefix, String properties, VariableResolver<String> vr, Set<String> propsToMask) throws IOException {
		if (properties == null) {
			return this;
		} else {
			properties = Util.replaceMacro(properties, propertiesGeneratingResolver(vr));
			Iterator i$ = Util.loadProperties(properties).entrySet().iterator();

			while(i$.hasNext()) {
				Entry<Object, Object> entry = (Entry)i$.next();
				this.addKeyValuePair(prefix, (String)entry.getKey(), entry.getValue().toString(), propsToMask == null ? false : propsToMask.contains(entry.getKey()));
			}

			return this;
		}
	}

	private static VariableResolver<String> propertiesGeneratingResolver(final VariableResolver<String> original) {
		return new VariableResolver<String>() {
			public String resolve(String name) {
				String value = (String)original.resolve(name);
				return value == null ? null : value.replaceAll("\\\\", "\\\\\\\\");
			}
		};
	}

	public String[] toCommandArray() {
		return (String[])this.args.toArray(new String[this.args.size()]);
	}

	public ArgumentListBuilder clone() {
		ArgumentListBuilder r = new ArgumentListBuilder();
		r.args.addAll(this.args);
		r.mask = (BitSet)this.mask.clone();
		return r;
	}

	public void clear() {
		this.args.clear();
		this.mask.clear();
	}

	public List<String> toList() {
		return this.args;
	}

	public String toStringWithQuote() {
		StringBuilder buf = new StringBuilder();
		Iterator i$ = this.args.iterator();

		while(true) {
			while(i$.hasNext()) {
				String arg = (String)i$.next();
				if (buf.length() > 0) {
					buf.append(' ');
				}

				if (arg.indexOf(32) < 0 && arg.length() != 0) {
					buf.append(arg);
				} else {
					buf.append('"').append(arg).append('"');
				}
			}

			return buf.toString();
		}
	}

	public ArgumentListBuilder toWindowsCommand(boolean escapeVars) {
		ArgumentListBuilder windowsCommand = (new ArgumentListBuilder()).add("cmd.exe", "/C");

		for(int i = 0; i < this.args.size(); ++i) {
			StringBuilder quotedArgs = new StringBuilder();
			String arg = (String)this.args.get(i);
			boolean percent = false;
			boolean quoted = false;

			for(int j = 0; j < arg.length(); ++j) {
				char c = arg.charAt(j);
				if (!quoted && (c == ' ' || c == '*' || c == '?' || c == ',' || c == ';')) {
					quoted = startQuoting(quotedArgs, arg, j);
				} else if (c != '^' && c != '&' && c != '<' && c != '>' && c != '|') {
					if (c == '"') {
						if (!quoted) {
							quoted = startQuoting(quotedArgs, arg, j);
						}

						quotedArgs.append('"');
					} else if (percent && escapeVars && (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')) {
						if (!quoted) {
							quoted = startQuoting(quotedArgs, arg, j);
						}

						quotedArgs.append('"').append(c);
						c = '"';
					}
				} else if (!quoted) {
					quoted = startQuoting(quotedArgs, arg, j);
				}

				percent = c == '%';
				if (quoted) {
					quotedArgs.append(c);
				}
			}

			if (i == 0 && quoted) {
				quotedArgs.insert(0, '"');
			} else if (i == 0 && !quoted) {
				quotedArgs.append('"');
			}

			if (quoted) {
				quotedArgs.append('"');
			} else {
				quotedArgs.append(arg);
			}

			windowsCommand.add((Object)quotedArgs, this.mask.get(i));
		}

		windowsCommand.add("&&").add("exit").add("%%ERRORLEVEL%%\"");
		return windowsCommand;
	}

	public ArgumentListBuilder toWindowsCommand() {
		return this.toWindowsCommand(false);
	}

	private static boolean startQuoting(StringBuilder buf, String arg, int atIndex) {
		buf.append('"').append(arg.substring(0, atIndex));
		return true;
	}

	public boolean hasMaskedArguments() {
		return this.mask.length() > 0;
	}

	public boolean[] toMaskArray() {
		boolean[] mask = new boolean[this.args.size()];

		for(int i = 0; i < mask.length; ++i) {
			mask[i] = this.mask.get(i);
		}

		return mask;
	}

	public void addMasked(String string) {
		this.add(string, true);
	}

//	public ArgumentListBuilder addMasked(Secret s) {
//		return this.add(Secret.toString(s), true);
//	}

	public String toString() {
		StringBuilder buf = new StringBuilder();

		for(int i = 0; i < this.args.size(); ++i) {
			String arg = (String)this.args.get(i);
			if (this.mask.get(i)) {
				arg = "******";
			}

			if (buf.length() > 0) {
				buf.append(' ');
			}

			if (arg.indexOf(32) < 0 && arg.length() != 0) {
				buf.append(arg);
			} else {
				buf.append('"').append(arg).append('"');
			}
		}

		return buf.toString();
	}
}

