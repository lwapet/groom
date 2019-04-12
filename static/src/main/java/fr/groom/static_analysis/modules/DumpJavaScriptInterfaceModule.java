package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import fr.groom.static_models.JavascriptInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

import java.util.ArrayList;
import java.util.List;

/**
 * This module dumps all JavascriptInterface classes in a given apk.
 */
public class DumpJavaScriptInterfaceModule extends Module<List<JavascriptInterface>> implements IModule {
	private static String storageField = "javascript_interfaces";

	public DumpJavaScriptInterfaceModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		unit.apply(new AbstractStmtSwitch() {
			@Override
			public void caseInvokeStmt(InvokeStmt stmt) {
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				SootMethod invokeExprMethod = invokeExpr.getMethod();
				if (invokeExprMethod.getSignature().equals(JavascriptInterface.JSI_SIGNATURE)) {
					JavascriptInterface jsi = new JavascriptInterface(invokeExpr, sootMethod, unit);
					resultHandler(jsi);
				}
			}
		});
	}

	@Override
	public void processResults() {
	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONArray arr = new JSONArray();
		this.data.forEach(jsi -> arr.put(jsi.toJson()));
		field.put(storageField, arr);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, field, Main.STATIC_COLLECTION);
	}

	@Override
	public void resultHandler(Object result) {
		this.data.add((JavascriptInterface) result);
	}

	@Override
	public void onFinish() {
		saveResults();
	}
}
