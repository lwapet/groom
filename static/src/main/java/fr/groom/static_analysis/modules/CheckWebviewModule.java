package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

/**
 * This module is responsible for checking whether an app is using the android.webkit.WebView class or not
 */

public class CheckWebviewModule extends Module<Boolean> implements IModule {
	boolean isAlreadySaved = false;

	public CheckWebviewModule(StaticAnalysis staticAnalysis) {
		super(false, ModuleType.UNITLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		if (unit.toString().contains("WebView")) {
			this.resultHandler(this.data);
		}
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		field.put("is_webview", this.data);
		dataUpdate.put("$set", field);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, dataUpdate, Main.STATIC_COLLECTION);
		this.isAlreadySaved = true;
	}

	@Override
	public void resultHandler(Object result) {
		this.data = true;
		if (!isAlreadySaved) {
			saveResults();
		}
	}

	@Override
	public void onFinish() {
		if(!isAlreadySaved) {
			saveResults();
		}
	}
}
