package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DumpSpecificMethodsModule extends Module<List<String>> implements IModule {
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

	public DumpSpecificMethodsModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
		monitoredSignaturesSet.addAll(Arrays.asList(monitoredSignatures));
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		Stmt stmt = (Stmt) unit;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			SootMethod invokedMethod = invokeExpr.getMethod();
			if (monitoredSignaturesSet.contains(invokedMethod.getSignature()))
				this.resultHandler(invokedMethod.getSignature());
		}
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		field.put(this.storageField, this.data);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, field, Main.STATIC_COLLECTION);
	}

	@Override
	public void resultHandler(Object result) {
			this.data.add((String) result);
	}

	@Override
	public void onFinish() {
		saveResults();
	}
}
