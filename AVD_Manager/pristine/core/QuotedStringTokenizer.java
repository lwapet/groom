package fr.groom.core;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class QuotedStringTokenizer extends StringTokenizer {
	private static final String __delim = " \t\n\r";
	private String _string;
	private String _delim;
	private boolean _returnQuotes;
	private boolean _returnDelimiters;
	private StringBuilder _token;
	private boolean _hasToken;
	private int _i;
	private int _lastStart;
	private boolean _double;
	private boolean _single;
	private static final String ESCAPABLE_CHARS = "\\\"' ";

	public static String[] tokenize(String str) {
		return (new QuotedStringTokenizer(str)).toArray();
	}

	public static String[] tokenize(String str, String delimiters) {
		return (new QuotedStringTokenizer(str, delimiters)).toArray();
	}

	public QuotedStringTokenizer(String str, String delim, boolean returnDelimiters, boolean returnQuotes) {
		super("");
		this._delim = " \t\n\r";
		this._returnQuotes = false;
		this._returnDelimiters = false;
		this._hasToken = false;
		this._i = 0;
		this._lastStart = 0;
		this._double = true;
		this._single = true;
		this._string = str;
		if (delim != null) {
			this._delim = delim;
		}

		this._returnDelimiters = returnDelimiters;
		this._returnQuotes = returnQuotes;
		if (this._delim.indexOf(39) < 0 && this._delim.indexOf(34) < 0) {
			this._token = new StringBuilder(this._string.length() > 1024 ? 512 : this._string.length() / 2);
		} else {
			throw new Error("Can't use quotes as delimiters: " + this._delim);
		}
	}

	public QuotedStringTokenizer(String str, String delim, boolean returnDelimiters) {
		this(str, delim, returnDelimiters, false);
	}

	public QuotedStringTokenizer(String str, String delim) {
		this(str, delim, false, false);
	}

	public QuotedStringTokenizer(String str) {
		this(str, (String)null, false, false);
	}

	public String[] toArray() {
		ArrayList r = new ArrayList();

		while(this.hasMoreTokens()) {
			r.add(this.nextToken());
		}

		return (String[])r.toArray(new String[r.size()]);
	}

	public boolean hasMoreTokens() {
		if (this._hasToken) {
			return true;
		} else {
			this._lastStart = this._i;
			int state = 0;
			boolean escape = false;

			while(true) {
				while(this._i < this._string.length()) {
					char c = this._string.charAt(this._i++);
					switch(state) {
						case 0:
							if (this._delim.indexOf(c) >= 0) {
								if (this._returnDelimiters) {
									this._token.append(c);
									return this._hasToken = true;
								}
							} else if (c == '\'' && this._single) {
								if (this._returnQuotes) {
									this._token.append(c);
								}

								state = 2;
							} else {
								if (c == '"' && this._double) {
									if (this._returnQuotes) {
										this._token.append(c);
									}

									state = 3;
									continue;
								}

								this._token.append(c);
								this._hasToken = true;
								state = 1;
							}
							break;
						case 1:
							this._hasToken = true;
							if (escape) {
								escape = false;
								if ("\\\"' ".indexOf(c) < 0) {
									this._token.append('\\');
								}

								this._token.append(c);
							} else {
								if (this._delim.indexOf(c) >= 0) {
									if (this._returnDelimiters) {
										--this._i;
									}

									return this._hasToken;
								}

								if (c == '\'' && this._single) {
									if (this._returnQuotes) {
										this._token.append(c);
									}

									state = 2;
								} else if (c == '"' && this._double) {
									if (this._returnQuotes) {
										this._token.append(c);
									}

									state = 3;
								} else if (c == '\\') {
									escape = true;
								} else {
									this._token.append(c);
								}
							}
							break;
						case 2:
							this._hasToken = true;
							if (escape) {
								escape = false;
								if ("\\\"' ".indexOf(c) < 0) {
									this._token.append('\\');
								}

								this._token.append(c);
							} else if (c == '\'') {
								if (this._returnQuotes) {
									this._token.append(c);
								}

								state = 1;
							} else if (c == '\\') {
								if (this._returnQuotes) {
									this._token.append(c);
								}

								escape = true;
							} else {
								this._token.append(c);
							}
							break;
						case 3:
							this._hasToken = true;
							if (escape) {
								escape = false;
								if ("\\\"' ".indexOf(c) < 0) {
									this._token.append('\\');
								}

								this._token.append(c);
							} else if (c == '"') {
								if (this._returnQuotes) {
									this._token.append(c);
								}

								state = 1;
							} else if (c == '\\') {
								if (this._returnQuotes) {
									this._token.append(c);
								}

								escape = true;
							} else {
								this._token.append(c);
							}
					}
				}

				return this._hasToken;
			}
		}
	}

	public String nextToken() throws NoSuchElementException {
		if (this.hasMoreTokens() && this._token != null) {
			String t = this._token.toString();
			this._token.setLength(0);
			this._hasToken = false;
			return t;
		} else {
			throw new NoSuchElementException();
		}
	}

	public String nextToken(String delim) throws NoSuchElementException {
		this._delim = delim;
		this._i = this._lastStart;
		this._token.setLength(0);
		this._hasToken = false;
		return this.nextToken();
	}

	public boolean hasMoreElements() {
		return this.hasMoreTokens();
	}

	public Object nextElement() throws NoSuchElementException {
		return this.nextToken();
	}

	public int countTokens() {
		return -1;
	}

	public static String quote(String s, String delim) {
		if (s == null) {
			return null;
		} else if (s.length() == 0) {
			return "\"\"";
		} else {
			for(int i = 0; i < s.length(); ++i) {
				char c = s.charAt(i);
				if (c == '\\' || c == '"' || c == '\'' || Character.isWhitespace(c) || delim.indexOf(c) >= 0) {
					StringBuffer b = new StringBuffer(s.length() + 8);
					quote(b, s);
					return b.toString();
				}
			}

			return s;
		}
	}

	public static String quote(String s) {
		if (s == null) {
			return null;
		} else if (s.length() == 0) {
			return "\"\"";
		} else {
			StringBuffer b = new StringBuffer(s.length() + 8);
			quote(b, s);
			return b.toString();
		}
	}

	public static void quote(StringBuffer buf, String s) {
		synchronized(buf) {
			buf.append('"');

			for(int i = 0; i < s.length(); ++i) {
				char c = s.charAt(i);
				switch(c) {
					case '\b':
						buf.append("\\b");
						break;
					case '\t':
						buf.append("\\t");
						break;
					case '\n':
						buf.append("\\n");
						break;
					case '\f':
						buf.append("\\f");
						break;
					case '\r':
						buf.append("\\r");
						break;
					case '"':
						buf.append("\\\"");
						break;
					case '\\':
						buf.append("\\\\");
						break;
					default:
						buf.append(c);
				}
			}

			buf.append('"');
		}
	}

	public static String unquote(String s) {
		if (s == null) {
			return null;
		} else if (s.length() < 2) {
			return s;
		} else {
			char first = s.charAt(0);
			char last = s.charAt(s.length() - 1);
			if (first != last || first != '"' && first != '\'') {
				return s;
			} else {
				StringBuilder b = new StringBuilder(s.length() - 2);
				boolean escape = false;

				for(int i = 1; i < s.length() - 1; ++i) {
					char c = s.charAt(i);
					if (escape) {
						escape = false;
						switch(c) {
							case 'b':
								b.append('\b');
								break;
							case 'c':
							case 'd':
							case 'e':
							case 'g':
							case 'h':
							case 'i':
							case 'j':
							case 'k':
							case 'l':
							case 'm':
							case 'o':
							case 'p':
							case 'q':
							case 's':
							default:
								b.append(c);
								break;
							case 'f':
								b.append('\f');
								break;
							case 'n':
								b.append('\n');
								break;
							case 'r':
								b.append('\r');
								break;
							case 't':
								b.append('\t');
								break;
							case 'u':
								b.append((char)((convertHexDigit((byte)s.charAt(i++)) << 24) + (convertHexDigit((byte)s.charAt(i++)) << 16) + (convertHexDigit((byte)s.charAt(i++)) << 8) + convertHexDigit((byte)s.charAt(i++))));
						}
					} else if (c == '\\') {
						escape = true;
					} else {
						b.append(c);
					}
				}

				return b.toString();
			}
		}
	}

	public boolean getDouble() {
		return this._double;
	}

	public void setDouble(boolean d) {
		this._double = d;
	}

	public boolean getSingle() {
		return this._single;
	}

	public void setSingle(boolean single) {
		this._single = single;
	}

	public static byte convertHexDigit(byte b) {
		if (b >= 48 && b <= 57) {
			return (byte)(b - 48);
		} else if (b >= 97 && b <= 102) {
			return (byte)(b - 97 + 10);
		} else {
			return b >= 65 && b <= 70 ? (byte)(b - 65 + 10) : 0;
		}
	}
}

