package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * This class dumps all CordovaPlugin classes in a given apk
 */
public class DumpCordovaPluginsModule extends Module<List<JSONObject>> implements IModule {
	private String storageField = "cordova_plugins";

	public DumpCordovaPluginsModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.SOOTCLASSLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		if (isCordovaPlugin(sootClass)) {
			JSONObject cordovaData = getJsonFromClass(sootClass);
			this.resultHandler(cordovaData);

		}
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		field.put(storageField, this.data);
		dataUpdate.put("$set", field);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, dataUpdate, Main.STATIC_COLLECTION);
	}

	@Override
	public void resultHandler(Object result) {
		this.data.add((JSONObject) result);
//		if (Configuration.v().getExcavatorOptions().isDumpProgressively()) {
//			this.saveResult((Document) result);
//		}
	}

	@Override
	public void onFinish() {
			saveResults();
	}

	private static boolean isCordovaPlugin(SootClass sootClass) {
		if (sootClass.getName().contains("CordovaPlugin")) {
			return true;
		} else if (sootClass.hasSuperclass()) {
			return isCordovaPlugin(sootClass.getSuperclass());
		} else {
			return false;
		}
	}

	private JSONObject getJsonFromClass(SootClass sootClass) {
		// TODO check className of plugin
		List<String> methodSignatures = new ArrayList<>();
		for (SootMethod sootMethod : sootClass.getMethods()) {
			methodSignatures.add(sootMethod.getSignature());
		}

		JSONObject data = new JSONObject();
		data.put("_id", new ObjectId());
		data.put("class", sootClass.getName());
		data.put("methods", methodSignatures);

		return data;
	}

}
