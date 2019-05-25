package fr.groom.models;

import org.json.JSONObject;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.*;

public class Receiver extends Component {
	public static String SUPER_CLASS_NAME = "android.content.BroadcastReceiver";
	public static String[] START_CALLBACKS = {
			"<android.content.BroadcastReceiver: void onReceive(android.content.Context,android.content.Intent)>"
	};
	public static String[] STOP_CALLBACKS = {
	};

	public static String[] START_ACTIVITY_ALT = {
			"<android.support.v7.app.AppCompatActivity: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<com.firebase.ui.auth.ui.HelperActivityBase: void startActivityForResult(android.content.Intent,int)>",
			"<com.google.android.gms.auth.api.signin.internal.SignInHubActivity: void startActivityForResult(android.content.Intent,int)>",
			"<android.app.Activity: void startActivityFromChild(android.app.Activity,android.content.Intent,int,android.os.Bundle)>",
			"<android.app.Activity: void startActivityFromFragment(android.app.Fragment,android.content.Intent,int,android.os.Bundle)>",
			"<android.support.v4.app.h: void startActivity(android.content.Intent)>",
			"<android.support.v4.app.h: void startActivityForResult(android.content.Intent,int)>",
			"<com.google.firebase.auth.internal.FederatedSignInActivity: void startActivityForResult(android.content.Intent,int)>",
			"<android.app.Fragment: void startActivityForResult(android.content.Intent,int,android.os.Bundle)>",
			"<android.app.Activity: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<io.yuka.android.Profile.PremiumStateActivity: void startActivity(android.content.Intent)>",
			"<android.app.Fragment: void startActivityForResult(android.content.Intent,int)>",
			"<android.app.Activity: void startActivity(android.content.Intent)>",
			"<io.yuka.android.Scan.ScanActivity: void startActivity(android.content.Intent)>",
			"<android.app.Activity: boolean startActivityIfNeeded(android.content.Intent,int)>",
			"<io.yuka.android.c.b: void startActivity(android.content.Intent)>",
			"<io.yuka.android.Premium.NewMemberActivity: void startActivity(android.content.Intent)>",
			"<com.firebase.ui.auth.ui.provider.GitHubLoginActivity: void startActivity(android.content.Intent)>",
			"<com.facebook.CustomTabActivity: void startActivityForResult(android.content.Intent,int)>",
			"<android.app.Fragment: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<io.yuka.android.EditProduct.f: void startActivityForResult(android.content.Intent,int)>",
			"<android.app.Fragment: void startActivity(android.content.Intent)>",
			"<com.firebase.ui.auth.ui.FragmentBase: void startActivityForResult(android.content.Intent,int)>",
			"<android.support.v4.app.h: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<com.facebook.internal.FragmentWrapper: void startActivityForResult(android.content.Intent,int)>",
			"<com.firebase.ui.auth.ui.email.EmailActivity: void startActivityForResult(android.content.Intent,int)>",
			"<io.yuka.android.EditProduct.NoGradeActivity: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<io.yuka.android.Main.RootActivity: void startActivityForResult(android.content.Intent,int)>",
			"<android.app.Activity: void startActivityFromFragment(android.app.Fragment,android.content.Intent,int)>",
			"<com.facebook.CustomTabMainActivity: void startActivity(android.content.Intent)>",
			"<io.yuka.android.EditProduct.EditProductActivity: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<com.facebook.share.DeviceShareDialog: void startActivityForResult(android.content.Intent,int)>",
			"<io.yuka.android.Premium.PremiumActivity: void startActivity(android.content.Intent)>",
			"<android.content.Context: void startActivity(android.content.Intent)>",
			"<android.app.Activity: void startActivityForResult(android.content.Intent,int,android.os.Bundle)>",
			"<android.support.v4.app.h: void startActivityForResult(android.content.Intent,int,android.os.Bundle)>",
			"<io.yuka.android.EditEmail.EditEmailActivity: void startActivity(android.content.Intent)>",
			"<io.yuka.android.EditProduct.NoGradeActivity: void startActivity(android.content.Intent)>",
			"<io.yuka.android.EditProduct.cosmetic.EditCosmetic4Activity: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<android.app.Activity: boolean startActivityIfNeeded(android.content.Intent,int,android.os.Bundle)>",
			"<android.content.Context: void startActivity(android.content.Intent,android.os.Bundle)>",
			"<com.google.android.gms.common.api.internal.g: void startActivityForResult(android.content.Intent,int)>",
			"<android.app.Activity: void startActivityFromChild(android.app.Activity,android.content.Intent,int)>",
			"<android.app.Activity: void startActivityForResult(android.content.Intent,int)>",
			"<com.facebook.login.StartActivityDelegate: void startActivityForResult(android.content.Intent,int)>",
			"<com.firebase.ui.auth.ui.email.WelcomeBackPasswordPrompt: void startActivity(android.content.Intent)>"
	};

	public static String[] START_SERVICE_ALT = {
			"<android.content.Context: android.content.ComponentName startService(android.content.Intent)>",
			"<android.content.Context: boolean bindService(android.content.Intent,android.content.ServiceConnection,int)>",
//			"<android.content.Context: void unbindService(android.content.ServiceConnection)>",
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

	public boolean launchActivity() {
		List<Type> types = new ArrayList<>();
		types.add(RefType.v("android.content.Context"));
		types.add(RefType.v("android.content.Intent"));
		SootMethod onReceiveMethod = this.getSootClass().getMethod("onReceive", types);
		onReceiveMethod.retrieveActiveBody();
//		return invokeMethod(onReceiveMethod, START_ACTIVITY_ALT, new HashSet<>());
//		return invokeMethod(onReceiveMethod,START_ACTIVITY_ALT,new JimpleBasedInterproceduralCFG(),new HashSet<>(),false);
		return invokeMethod(onReceiveMethod, START_ACTIVITY_ALT, new HashSet<>(), false);
	}

	private static boolean invokeMethod(SootMethod method, String[] signatureToCheck, JimpleBasedInterproceduralCFG jimpleBasedInterproceduralCFG, HashSet<Unit> unitPassed, boolean result) {
		if (method.isConcrete() && method.hasActiveBody()) {
			method.retrieveActiveBody();
			Set<Unit> callsFromWithin = jimpleBasedInterproceduralCFG.getCallsFromWithin(method);
			for (Unit u : callsFromWithin) {
				if (unitPassed.contains(u)) {
					continue;
				} else {
					unitPassed.add(u);
				}
				Stmt s = (Stmt) u;
				if (s.containsInvokeExpr()) {
					SootMethodRef sootMethodRef = s.getInvokeExpr().getMethodRef();
					if (Arrays.stream(signatureToCheck).anyMatch(sigToCheck -> sootMethodRef.getSignature().equals(sigToCheck))) {
						return true;
					} else {
						SootMethod next = sootMethodRef.tryResolve();
						if (next != null) {
							result = invokeMethod(next, signatureToCheck, jimpleBasedInterproceduralCFG, unitPassed, result);
							if (result)
								return true;
						}
					}
				}
			}
		}
		return result;
	}

	public boolean launchService() {
		List<Type> types = new ArrayList<>();
		types.add(RefType.v("android.content.Context"));
		types.add(RefType.v("android.content.Intent"));
		SootMethod onReceiveMethod = this.getSootClass().getMethod("onReceive", types);
		return invokeMethod(onReceiveMethod, START_SERVICE_ALT, new HashSet<>(), false);
	}

	private static boolean invokeMethod(SootMethod sootMethod, String[] signatureToCheck, HashSet<String> passed, boolean result) {
		if (sootMethod.isConcrete()) {
			passed.add(sootMethod.getSignature());
			Body onReceiveBody = sootMethod.retrieveActiveBody();
			for (Unit u : onReceiveBody.getUnits()) {
				if (Arrays.stream(signatureToCheck).anyMatch(s -> u.toString().contains(s))) {
					return true;
				} else {
					Stmt stmt = (Stmt) u;
					if (stmt.containsInvokeExpr()) {
						InvokeExpr e = stmt.getInvokeExpr();
						SootMethod next = e.getMethod();
						if (!passed.contains(next.getSignature())) {
							result = invokeMethod(e.getMethod(), signatureToCheck, passed, result);
							if (result)
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public JSONObject toJson() {
		JSONObject data = super.toJson();
		data.put("start_activity", launchActivity());
		data.put("start_service", launchService());
		return data;
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
