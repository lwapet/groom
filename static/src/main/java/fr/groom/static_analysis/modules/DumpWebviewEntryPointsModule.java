package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import fr.groom.models.WebviewEntryPoint;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

import java.util.ArrayList;
import java.util.List;

public class DumpWebviewEntryPointsModule extends Module<List<WebviewEntryPoint>> implements IModule {
	private String storageField = "webview_entry_points";

	public DumpWebviewEntryPointsModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		unit.apply(new AbstractStmtSwitch() {
			@Override
			public void caseInvokeStmt(InvokeStmt stmt) {
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				SootMethod invokeExprMethod = invokeExpr.getMethod();
				if (WebviewEntryPoint.isEntryPoint(invokeExprMethod.getSignature())) {
					WebviewEntryPoint entryPoint = new WebviewEntryPoint(sootMethod, unit);
					resultHandler(entryPoint);
				}
			}
		});
	}

	@Override
	public void processResults() {
		for (WebviewEntryPoint entryPoint : this.data) {
			processResult(entryPoint);
		}
	}

	public void processResult(WebviewEntryPoint webviewEntryPoint) {
		webviewEntryPoint.analyzeArguments();
	}

	@Override
	public void saveResults() {
//		this.dataHandler.updateAnalysis(this.getFinalDocument());
	}

	@Override
	public void resultHandler(Object result) {
		WebviewEntryPoint entryPoint = (WebviewEntryPoint) result;
		this.data.add(entryPoint);
//		if (Configuration.v().getExcavatorOptions().isDumpProgressively()) {
//			processResult(entryPoint);
//			saveResult(entryPoint);
//		}
	}

	@Override
	public void onFinish() {
			processResults();
			saveResults();
	}

	public void saveResult(WebviewEntryPoint entryPoint) {
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		this.data.forEach(wep -> field.accumulate(storageField, wep.toJson()));
		dataUpdate.put("$set", field);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, dataUpdate, Main.STATIC_COLLECTION);
//		this.dataHandler.updateAnalysis(update);
	}

}
