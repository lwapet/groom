package fr.groom.apk_instrumentation;

import fr.groom.Storage;
import fr.groom.mongo.Database;
import fr.groom.static_analysis.StaticAnalysis;
import fr.groom.models.Application;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.*;

public class SootInstrumenter extends SceneTransformer {
	private Hooker hooker;
	private StaticAnalysis staticAnalysis;
	private String[] excludedClasses = {"MethodLogger", InstrumenterUtils.groomClassName, "fakeActivity"};
	private String[] reflectionInvokeMethods = {
			"<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>"
	};
	private static String[] sensitiveMethod = {
			"<android.view.ViewManager: void addView(android.view.View,android.view.ViewGroup$LayoutParams)>",
//			"<android.view.LayoutInflater: android.view.View inflate(int,android.view.ViewGroup)>",
			"<android.content.Context: android.content.Intent registerReceiver(android.content.BroadcastReceiver,android.content.IntentFilter)>",
	};
	private List<String> methodInvokesToHook = new ArrayList<>();
	int statementHookedCount;
	int methodHookedCount;
	int unitSeenCount;

	public SootInstrumenter(Application app, StaticAnalysis staticAnalysis) {
		this.hooker = new Hooker(app, this);
		this.staticAnalysis = staticAnalysis;
	}

	private void onStart() {
		methodInvokesToHook.addAll(Arrays.asList(sensitiveMethod));
		if (staticAnalysis != null) {
			methodInvokesToHook.addAll(staticAnalysis.getSources());
			methodInvokesToHook.addAll(staticAnalysis.getSinks());
		}
	}

	private void onFinish() {
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
			if(methodInvokesToHook.stream().anyMatch(s -> s.equals(invokeExpr.getMethod().getSignature()))) {
				hooker.hookInvokeExpr(sootMethod, unit, invokeExpr);
			}
			if (stmt.getInvokeExpr().getMethod().getParameterTypes().stream().anyMatch(t -> t.equals(Hooker.INTENT_TYPE))) {
				hooker.hookInvokeExpr(sootMethod, unit, invokeExpr);
			}
			if (Arrays.asList(reflectionInvokeMethods).contains(stmt.getInvokeExpr().getMethod().getSignature())) {
				hooker.hookReflectionMethod(sootMethod, unit, stmt.getInvokeExpr());
			}
		}
	}

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		onStart();
		Iterator<SootClass> sootClassIterator = Scene.v().getApplicationClasses().snapshotIterator();
		while (sootClassIterator.hasNext()) {
			final SootClass sootClass = sootClassIterator.next();
			if(sootClass.getName().contains("anonymous.com") && sootClass.getName().contains("MainActivity")) {
				System.out.println("cii");
			}
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

	public int getMethodHookedCount() {
		return methodHookedCount;
	}

	public int getUnitSeenCount() {
		return unitSeenCount;
	}
}
