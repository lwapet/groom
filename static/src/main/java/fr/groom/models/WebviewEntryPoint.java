package fr.groom.models;

import fr.groom.static_analysis.StringAnalysis;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class WebviewEntryPoint {
	private static final String[] ENTRYPOINT_SIGNATURES = {
			"<android.webkit.WebView: void loadDataWithBaseURL(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)>",
			"<android.webkit.WebView: void loadUrl(java.lang.String,java.util.Map)>",
			"<android.webkit.WebView: void loadUrl(java.lang.String)>",
			"<android.webkit.WebView: void postUrl(java.lang.String,byte[])>",
			"<android.webkit.WebView: void loadData(java.lang.String,java.lang.String,java.lang.String)>",
			"<android.webkit.WebView: void evaluateJavascript(java.lang.String,android.webkit.ValueCallback)>"
	};
	private HashMap<String, List<String>> argumentAnalysis;
	private SootMethod caller;
	private Unit unitCall;
	private List<Document> argumentAnalyses;
	private String type;

	public WebviewEntryPoint(SootMethod caller, Unit unitCall) {
		this.argumentAnalysis = new HashMap<>();
		this.caller = caller;
		this.unitCall = unitCall;
		this.argumentAnalyses = new ArrayList<>();
	}

	public static boolean isEntryPoint(String signature) {
		for (String entryPointSignature : ENTRYPOINT_SIGNATURES) {
			if (signature.equals(entryPointSignature)) {
				return true;
			}
		}
		return false;
	}

	public void analyzeArguments() {
		InvokeExpr invokeExpr = this.getInvokeExpr();
		for (int i = 0; i < invokeExpr.getArgCount(); i++) {
			Value arg = invokeExpr.getArg(i);
			StringAnalysis stringAnalysis = new StringAnalysis(this.caller, this.unitCall, arg);
			List<String> results = this.analyzeArgument(stringAnalysis);
			for (String result : results) {
				if (result.contains("http://") || result.contains("https://")) {
					this.type = "remote";
				} else if (result.contains("file:///")) {
					this.type = "local";
				} else {
					this.type = "unknown";
				}
			}
			this.argumentAnalysis.put("arg" + i, results);
		}
	}

	private InvokeExpr getInvokeExpr() {
		Stmt stmt = (Stmt) this.unitCall;
		return stmt.getInvokeExpr();
	}

	private String getEntryPointSignature() {
		return getInvokeExpr().getMethod().getSignature();
	}


	public List<String> analyzeArgument(StringAnalysis stringAnalysis) {
		stringAnalysis.analyze();
		TupleStringInt results = stringAnalysis.evaluateEasy();
		List<String> strings = results.getStrings();
		List<String> distincts = strings.stream().distinct().collect(Collectors.toList());
		distincts.remove("");
		return distincts;
	}


	public JSONObject toJson() {
		JSONObject record = new JSONObject();
		record.put("_id", new ObjectId());
		record.put("signature", getEntryPointSignature());
		record.put("caller_method", this.caller.getSignature());
		record.put("caller_method_class", this.caller.getDeclaringClass().getName());
		record.put("strings", this.argumentAnalysis);
		record.put("type", this.type);

		return record;
	}
}
