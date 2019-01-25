package fr.groom.static_analysis.modules;

import com.sun.org.apache.xpath.internal.operations.Mod;
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
import java.util.List;

public class DumpMethodUnitModule extends Module<List<String>> implements IModule {
	String storageField = "method_statements";

	public DumpMethodUnitModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		Stmt stmt = (Stmt) unit;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			SootMethod invokedMethod = invokeExpr.getMethod();
			if (!invokedMethod.getSignature().startsWith("<java."))
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
