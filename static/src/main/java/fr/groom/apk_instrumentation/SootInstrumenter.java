package fr.groom.apk_instrumentation;

import fr.groom.models.Application;
import fr.groom.models.WebviewEntryPoint;
import fr.groom.static_analysis.StaticAnalysis;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition;
import soot.jimple.infoflow.sourcesSinks.definitions.SourceSinkDefinition;

import java.util.*;

public class SootInstrumenter extends SceneTransformer {
	private static String typeKey = "type";
	private static String categoryKey = "category";
	private static String signatureKey = "signature";
	private Hooker hooker;
	private StaticAnalysis staticAnalysis;
	private String[] excludedClasses = {"MethodLogger", InstrumenterUtils.groomClassName, "fakeActivity"};
	private String[] reflectionInvokeMethods = {
			"<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>"
	};
	private String[] subprocessMethods = {
			"<java.lang.ProcessBuilder: java.lang.Process start()>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String[])>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String[],java.lang.String[])>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String[],java.lang.String[],java.io.File)>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String)>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String,java.lang.String[])>",
			"<java.lang.Runtime: java.lang.Process exec(java.lang.String,java.lang.String[],java.io.File)>",
	};
	private String[] nativeCodeLoadingMethods = {
			"<java.lang.System: void loadLibrary(java.lang.String)>",
			"<java.lang.System: void load(java.lang.String)>",
	};
	private String[] cryptoMethods = {
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
	private HashSet<InvokeExpr> invokeExprs = new HashSet<>();
	public HashSet<Unit> injectedUnits = new HashSet<>();
	//	private static String[] sensitiveMethod = {
//			"<android.view.ViewManager: void addView(android.view.View,android.view.ViewGroup$LayoutParams)>",
//			"<android.view.LayoutInflater: android.view.View inflate(int,android.view.ViewGroup)>",
//			"<android.content.Context: android.content.Intent registerReceiver(android.content.BroadcastReceiver,android.content.IntentFilter)>",
//	};
	//	private List<String> methodInvokesToHook = new ArrayList<>();
	private HashMap<String, HashMap<String, String>> methodsToHook = new HashMap<>();
	int statementHookedCount;
	int injectedUnitsCount;
	int unitSeenCount;

	public SootInstrumenter(Application app, StaticAnalysis staticAnalysis) {
		this.hooker = new Hooker(app, this);
		this.staticAnalysis = staticAnalysis;
	}

	private void onStart() {
		HashMap<String, String> v1 = new HashMap<>();
		v1.put(typeKey, "overlay_method");
		methodsToHook.put("<android.view.ViewManager: void addView(android.view.View,android.view.ViewGroup$LayoutParams)>", v1);
		HashMap<String, String> v2 = new HashMap<>();
		v2.put(typeKey, "dynamic_receiver_registration");
		methodsToHook.put("<android.content.Context: android.content.Intent registerReceiver(android.content.BroadcastReceiver,android.content.IntentFilter)>", v2);
		System.out.println("Loading sources and sinks.");

		for (String cryptoSignature : cryptoMethods) {
			HashMap<String, String> v = new HashMap<>();
			v.put(typeKey, "crypto_method");
			methodsToHook.put(cryptoSignature, v);
		}
		for (String subprocessSignature : subprocessMethods) {
			HashMap<String, String> v = new HashMap<>();
			v.put(typeKey, "subprocess_method");
			methodsToHook.put(subprocessSignature, v);
		}

		for (String nativeSignature : nativeCodeLoadingMethods) {
			HashMap<String, String> v = new HashMap<>();
			v.put(typeKey, "nativecodeloading_method");
			methodsToHook.put(nativeSignature, v);
		}

		for(String webviewEntryPoint : WebviewEntryPoint.ENTRYPOINT_SIGNATURES) {
			HashMap<String, String> v = new HashMap<>();
			v.put(typeKey, "webview_entry_point_method");
			methodsToHook.put(webviewEntryPoint, v);
		}
//		methodInvokesToHook.addAll(Arrays.asList(sensitiveMethod));
		for (SourceSinkDefinition def : this.staticAnalysis.getProvider().getSources()) {
			MethodSourceSinkDefinition methodDef = (MethodSourceSinkDefinition) def;
			String category = methodDef.getCategory().getID();
			String signature = methodDef.getMethod().getSignature();
			HashMap<String, String> infos = new HashMap<>();
			infos.put(typeKey, "source");
			infos.put(categoryKey, category);
			methodsToHook.put(signature, infos);
		}
		for (SourceSinkDefinition def : this.staticAnalysis.getProvider().getSinks()) {
			MethodSourceSinkDefinition methodDef = (MethodSourceSinkDefinition) def;
			String category = methodDef.getCategory().getID();
			String signature = methodDef.getMethod().getSignature();
			HashMap<String, String> infos = new HashMap<>();
			infos.put(typeKey, "sink");
			infos.put(categoryKey, category);
			methodsToHook.put(signature, infos);
		}
		System.out.println("Added all sources and sinks.");
	}

	private void onFinish() {
		System.out.println("Finished iteration through all units");
		hooker.hookCallbacks();
	}


	private void handleMethod(SootClass sootClass, SootMethod sootMethod) {
//		if (hooker.isMethodCompliant(sootMethod))
//			hooker.hookMethod(sootMethod);
	}

	private void handleUnit(SootClass sootClass, SootMethod sootMethod, Unit unit) {
//		this.provider.getSources().forEach(sourceSinkDefinition -> {
//			MethodSourceSinkDefinition m = (MethodSourceSinkDefinition) sourceSinkDefinition;
//			if(unit.toString().contains(m.getMethod().getSignature())) {
//				System.out.println(m.getMethod().getSignature());
//			}
//		});
//		this.provider.getSinks().forEach(sourceSinkDefinition -> {
//			MethodSourceSinkDefinition m = (MethodSourceSinkDefinition) sourceSinkDefinition;
//			if(unit.toString().contains(m.getMethod().getSignature())) {
//				System.out.println(m.getMethod().getSignature());
//			}
//		});
		Stmt stmt = (Stmt) unit;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			if (stmt.getInvokeExpr().getMethod().getParameterTypes().stream().anyMatch(t -> t.equals(Hooker.INTENT_TYPE)) && !invokeExprs.contains(invokeExpr) && !injectedUnits.contains(unit)) {
				HashMap<String, String> data = new HashMap<>();
				data.put(signatureKey, invokeExpr.getMethod().getSignature());
				data.put(typeKey, "intent_arg_method");
//				hooker.hookExpression(sootMethod, unit, invokeExpr, "intent_arg_method");
				invokeExprs.add(invokeExpr);
			}
			if (methodsToHook.containsKey(invokeExpr.getMethod().getSignature()) && !invokeExprs.contains(invokeExpr) && !injectedUnits.contains(unit)) {
				HashMap<String, String> infos = methodsToHook.get(invokeExpr.getMethod().getSignature());
//				hooker.hookInvokeExpr(sootMethod, unit, invokeExpr, category);
				HashMap<String, String> data = new HashMap<>();
				data.put("signature", invokeExpr.getMethod().getSignature());
				if (infos.containsKey(typeKey) && infos.get(typeKey) != null)
					data.put(typeKey, infos.get(typeKey));
				if (infos.containsKey(categoryKey) && infos.get(categoryKey) != null)
					data.put(categoryKey, infos.get(categoryKey));
				hooker.hookExpression(sootMethod, unit, invokeExpr, data);
				invokeExprs.add(invokeExpr);
			}
//			if (methodsToHook.stream().anyMatch(s -> s.equals(invokeExpr.getMethod().getSignature()))) {
//				hooker.hookInvokeExpr(sootMethod, unit, invokeExpr);
//			}
			if (Arrays.asList(reflectionInvokeMethods).contains(stmt.getInvokeExpr().getMethod().getSignature())) {
				HashMap<String, String> data = new HashMap<>();
				data.put(signatureKey, invokeExpr.getMethod().getSignature());
				data.put(typeKey, "reflection_call");
				hooker.hookExpression(sootMethod, unit, stmt.getInvokeExpr(), data);
//				hooker.hookReflectionMethod(sootMethod, unit, stmt.getInvokeExpr());
			}
		}
	}

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		onStart();
		Iterator<SootClass> sootClassIterator = Scene.v().getApplicationClasses().snapshotIterator();
		System.out.println("Starting soot instrumenter iteration through every unit.");
		while (sootClassIterator.hasNext()) {
			final SootClass sootClass = sootClassIterator.next();
			if (Arrays.stream(excludedClasses).anyMatch(s -> sootClass.getName().equals(s) || sootClass.getName().contains("fr.groom"))) {
				continue;
			}
			if (sootClass.getName().startsWith("android.support")) {
				continue;
			}
			List<SootMethod> clone = new ArrayList<>(sootClass.getMethods());
			for (SootMethod sootMethod : clone) {
				if (sootMethod.isConcrete()) {
					Body body;
					if (!sootMethod.hasActiveBody()) {
						body = sootMethod.retrieveActiveBody();
					} else {
						body = sootMethod.getActiveBody();
					}
					for (Iterator<Unit> uIterator = body.getUnits().snapshotIterator();
						 uIterator.hasNext(); ) {
						Unit unit = uIterator.next();
						handleUnit(sootClass, sootMethod, unit);
						unitSeenCount += 1;
					}
					handleMethod(sootClass, sootMethod);
				}
			}
		}
		onFinish();
	}

	public int getStatementHookedCount() {
		return statementHookedCount;
	}

	public int getInjectedUnitsCount() {
		return injectedUnitsCount;
	}

	public int getUnitSeenCount() {
		return unitSeenCount;
	}
}
