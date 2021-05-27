package fr.groom.models;

import soot.Scene;
import soot.SootClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Activity extends Component {
	public static String SUPER_CLASS_NAME = "android.app.Activity";

	public static String[] START_CALLBACKS = {
			"<android.app.Activity: void onCreate(android.os.Bundle)>",
			"<android.app.Activity: void onStart()>",
			"<android.app.Activity: void onResume()>",
			"<android.app.Activity: void onRestart()>",
	};
	public static String[] STOP_CALLBACKS = {
			"<android.app.Activity: void onDestroy()>",
			"<android.app.Activity: void onStop()>",
			"<android.app.Activity: void onPause()>"
	};
	public static String[] START_ACTIVITY = {
			"<android.content.ContextWrapper: void startActivity(android.content.Intent)>",
			"<android.app.Service: void startActivity(android.content.Intent,android.os.Bundle)>"
	};

	public static HashSet<Callback> ORIGINAL_CALLBACKS;

	static {
		ORIGINAL_CALLBACKS = new HashSet<>();
		Arrays.stream(START_CALLBACKS).forEach(cs -> {
			ORIGINAL_CALLBACKS.add(new Callback(cs, true));
		});
		Arrays.stream(STOP_CALLBACKS).forEach(cs -> {
			ORIGINAL_CALLBACKS.add(new Callback(cs, false));
		});

	}

	HashMap<Callback, Integer> callbackBuffer;

	Activity(SootClass sootClass, int idNumber, List<IntentFilter> intentFilters) {
		super(sootClass, Scene.v().getSootClass(SUPER_CLASS_NAME), "A" + idNumber, intentFilters);
		this.callbackBuffer = new HashMap<>();
	}


	@Override
	public void apply(Switch sw) {
		((ComponentSwitch) sw).caseActivity(this);
	}

	@Override
	public HashSet<Callback> getOriginalCallbacks() {
		return ORIGINAL_CALLBACKS;
	}
}

