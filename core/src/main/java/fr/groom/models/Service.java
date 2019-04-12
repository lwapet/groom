package fr.groom.models;

import soot.Scene;
import soot.SootClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Service extends Component {
	public static String SUPER_CLASS_NAME = "android.app.Service";
	public static String[] START_CALLBACKS = {
//			"<android.app.Service: android.os.IBinder onBind(android.content.Intent)>",
			"<android.app.Service: void onCreate()>",
			"<android.app.Service: void onStart(android.content.Intent,int)>"
	};
	public static String[] STOP_CALLBACKS = {
			"<android.app.Service: void onDestroy()>",
	};
	public static String START_SERVICE = "<android.app.Activity: void startService(android.content.Intent,android.os.Bundle)>";

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


	Service(SootClass sootClass, int idNumber, List<IntentFilter> intentFilters) {
		super(sootClass, Scene.v().getSootClass(SUPER_CLASS_NAME), "S" + idNumber, intentFilters);
	}

	@Override
	public void apply(Switch sw) {
		((ComponentSwitch) sw).caseService(this);
	}

	@Override
	public HashSet<Callback> getOriginalCallbacks() {
		return ORIGINAL_CALLBACKS;
	}
}
