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
 * This module dump all the app classes contained in a given apk
 */
public class DumpClassModule extends Module<List<String>> implements IModule {
	String storageField = "classes";

	public DumpClassModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.SOOTCLASSLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		if (!sootClass.getName().contains("android.support")) {
			this.resultHandler(sootClass.getName());
		}
	}

	@Override
	public void processResults() {
	}

	@Override
	public void saveResults() {
//		this.dataHandler.updateAnalysis(this.getFinalDocument());
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		field.put(this.storageField, this.data);
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
