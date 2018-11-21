package fr.groom.models;

import models.Switch;
import soot.Scene;
import soot.SootClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Receiver extends Component {
	public static String SUPER_CLASS_NAME = "android.content.BroadcastReceiver";
	public static String[] START_CALLBACKS = {
			"<android.content.BroadcastReceiver: void onReceive(android.content.Context,android.content.Intent)>"
	};
	public static String[] STOP_CALLBACKS = {
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

	Receiver(SootClass sootClass, int idNumber, List<IntentFilter> intentFilters) {
		super(sootClass, Scene.v().getSootClass(SUPER_CLASS_NAME), "R" + idNumber, intentFilters);
	}

	@Override
	public void apply(Switch sw) {
		((ComponentSwitch) sw).caseReceiver(this);
	}

	@Override
	public HashSet<Callback> getOriginalCallbacks() {
		return ORIGINAL_CALLBACKS;
	}
}
