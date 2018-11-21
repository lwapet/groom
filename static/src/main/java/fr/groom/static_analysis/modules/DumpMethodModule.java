package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * This module dumps all the application (framework methods excluded) in a given apk
 */
public class DumpMethodModule extends Module<List<String>> implements IModule {
	String storageField = "methods";

	public DumpMethodModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.SOOTMETHODLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		if (!sootMethod.getSignature().startsWith("<java."))
			this.resultHandler(sootMethod.getSignature());
	}

	@Override
	public void processResults() {
	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		this.data.forEach(s -> field.accumulate(this.storageField, s));
		dataUpdate.put("$set", field);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, dataUpdate, Main.STATIC_COLLECTION);
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
