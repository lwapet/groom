package fr.groom.models;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IntentFilterData {
	private List<String> schemes;
	private List<String> hosts;
	private List<String> ports;
	private List<String> paths;
	private List<String> pathPatterns;
	private List<String> pathPrefixes;
	private List<String> mimeTypes;


	public final void addScheme(String scheme) {
		if (schemes == null) schemes = new ArrayList<String>();
		if (!schemes.contains(scheme)) {
			schemes.add(scheme.intern());
		}
	}

	public final void addHost(String host) {
		if (hosts == null) hosts = new ArrayList<String>();
		if (!hosts.contains(host)) {
			hosts.add(host.intern());
		}
	}

	public final void addPort(String port) {
		if (ports == null) ports = new ArrayList<String>();
		if (!ports.contains(port)) {
			ports.add(port);
		}
	}

	public final void addPath(String path) {
		if (paths == null) paths = new ArrayList<String>();
		if (!paths.contains(path)) {
			paths.add(path.intern());
		}
	}

	public final void addPathPattern(String pathPattern) {
		if (pathPatterns == null) pathPatterns = new ArrayList<String>();
		if (!pathPatterns.contains(pathPattern)) {
			pathPatterns.add(pathPattern.intern());
		}
	}

	public final void addPathPrefix(String pathPrefix) {
		if (pathPrefixes == null) pathPrefixes = new ArrayList<String>();
		if (!pathPrefixes.contains(pathPrefix)) {
			pathPrefixes.add(pathPrefix.intern());
		}
	}

	public final void addMimeType(String mimeType) {
		if (mimeTypes == null) mimeTypes = new ArrayList<String>();
		if (!mimeTypes.contains(mimeType)) {
			mimeTypes.add(mimeType.intern());
		}
	}


	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		if (schemes != null) {
			for (String s : schemes) {
				jo.accumulate("schemes", s);
			}
		}
		if (hosts != null) {
			for (String h : hosts) {
				jo.accumulate("hosts", h);
			}
		}
		if (ports != null) {
			for (String p : ports) {
				jo.accumulate("ports", p);
			}
		}
		if (paths != null) {
			for (String p : paths) {
				jo.accumulate("paths", p);
			}
		}
		if (pathPatterns != null) {
			for (String p : pathPatterns) {
				jo.accumulate("pathPatterns", p);
			}
		}
		if (pathPrefixes != null) {
			for (String p : pathPrefixes) {
				jo.accumulate("pathPrefixes", p);
			}
		}
		if (mimeTypes != null) {
			for (String m : mimeTypes) {
				jo.accumulate("mimeTypes", m);
			}
		}
		return jo;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
