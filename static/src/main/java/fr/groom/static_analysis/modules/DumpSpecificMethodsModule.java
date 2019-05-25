package fr.groom.static_analysis.modules;

import com.mongodb.util.JSON;
import fr.groom.FileUtils;
import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import org.json.JSONArray;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.*;

public class DumpSpecificMethodsModule extends Module<List<String>> implements IModule {
	private static HashMap<String, HashSet<String>> monitoredMethods;
	private  HashMap<String, HashMap<String,Integer>> caughtSignatures;

	private static String[] monitoredSignatures = {
			"<java.lang.ProcessBuilder: java.lang.Process start()>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String[])>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String[],java.lang.String[])>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String[],java.lang.String[],java.io.File)>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String)>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String,java.lang.String[])>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String,java.lang.String[],java.io.File)>",
			"<java.lang.System: void loadLibrary(java.lang.String)>",
			"<java.lang.System: void load(java.lang.String)>",
			"<javax.crypto.Cipher: javax.crypto.Cipher getInstance(java.lang.String)>",
			"<javax.crypto.spec.SecretKeySpec: void <init>(byte[],java.lang.String)>",
			"<javax.crypto.Cipher: void init(int,java.security.Key,java.security.spec.AlgorithmParameterSpec)>",
			"<javax.crypto.CipherInputStream: void <init>(java.io.InputStream,javax.crypto.Cipher)>",
			"<javax.crypto.CipherInputStream: void close()>",
			"<javax.crypto.Cipher: void init(int,java.security.Key,java.security.spec.AlgorithmParameterSpec)>",
			"<javax.crypto.CipherOutputStream: void <init>(java.io.OutputStream,javax.crypto.Cipher)>",
			"<javax.crypto.CipherOutputStream: void flush()>",
			"<javax.crypto.CipherOutputStream: void close()>",
			"<javax.crypto.CipherOutputStream: void write(byte[],int,int)>",
			"<javax.crypto.spec.IvParameterSpec: void <init>(byte[])>"
	};
	private static HashSet<String> monitoredSignaturesSet = new HashSet<>();
	String storageField = "monitored_methods";
	HashSet<String> signatures = new HashSet<>();

	public DumpSpecificMethodsModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
		monitoredMethods = getMonitoredMethods();
		caughtSignatures = new HashMap<>();
//		monitoredSignaturesSet.addAll(Arrays.asList(monitoredSignatures));
	}

	public static HashMap<String, HashSet<String>> getMonitoredMethods() {
		HashMap<String,Object> data = FileUtils.getJsonAsHashMap("required_files/monitored_method_signatures.json");
		HashMap<String,HashSet<String>> monitoredMethods = new HashMap<>();
		data.forEach((s,o) -> {
			List<String> sigs = (List<String>)o;
			HashSet<String> set = new HashSet<String>(sigs);
			monitoredMethods.put(s,set);
		});
		return monitoredMethods;
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		if(!sootClass.isApplicationClass()) {
			return;
		}
		Stmt stmt = (Stmt) unit;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			SootMethod invokedMethod = invokeExpr.getMethod();
			if (invokedMethod != null) {
				monitoredMethods.forEach((key,set) -> {
					if(set.contains(invokedMethod.getSignature())) {

						HashMap<String,Integer> category = this.caughtSignatures.getOrDefault(key, new HashMap<>());
						Integer count = category.getOrDefault(invokedMethod.getSignature(),0);
						count += 1;
						category.put(invokedMethod.getSignature(),count);
						this.caughtSignatures.put(key,category);
					}
				});

			}
		}
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONArray caughtSignaturesJson = new JSONArray();
		this.caughtSignatures.forEach((typeKey, map)-> {
			JSONArray a = new JSONArray();
			map.forEach((method,count)-> {
				JSONObject o = new JSONObject();
				o.put("method", method);
				o.put("count",count);
				a.put(o);
			});
			JSONObject o = new JSONObject();
			o.put(typeKey,a);
			caughtSignaturesJson.put(o);
		});
		field.put(this.storageField, caughtSignaturesJson);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, field, Main.STATIC_COLLECTION);
	}

	@Override
	public void resultHandler(Object result) {
	}

	@Override
	public void onFinish() {
		saveResults();
	}
}
