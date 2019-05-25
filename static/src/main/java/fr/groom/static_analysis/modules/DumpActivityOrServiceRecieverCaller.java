package fr.groom.static_analysis.modules;

import fr.groom.models.Activity;
import fr.groom.models.Service;
import fr.groom.static_analysis.StaticAnalysis;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

import java.util.*;

public class DumpActivityOrServiceRecieverCaller extends Module<List<String>> implements IModule {
	private HashSet<String> startActivities = new HashSet<>();
	private HashSet<String> startServices = new HashSet<>();
//	JimpleBasedInterproceduralCFG jimpleBasedInterproceduralCFG = new JimpleBasedInterproceduralCFG();


	public DumpActivityOrServiceRecieverCaller(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);

	}

	public static boolean calledByReceiver(SootMethod sootMethod, JimpleBasedInterproceduralCFG jimpleBasedInterproceduralCFG, boolean result, HashSet<Unit> passedUnits) {
		Collection<Unit> callers = jimpleBasedInterproceduralCFG.getCallersOf(sootMethod);
		for (Unit caller : callers) {
			if(passedUnits.contains(caller)) {
				continue;
			}else {
				passedUnits.add(caller);
			}
			SootMethod methodCaller = jimpleBasedInterproceduralCFG.getMethodOf(caller);
			if (methodCaller != null) {
				if (methodCaller.getName().equals("onReceive")) {
					return true;
				} else {
					result = calledByReceiver(methodCaller, jimpleBasedInterproceduralCFG, result, passedUnits);
					if (result) {
						return true;
					}
				}
			}
		}
		return result;
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		Stmt s = (Stmt) unit;
		if (s.containsInvokeExpr()) {
			SootMethodRef mr = s.getInvokeExpr().getMethodRef();
			if (mr.getSignature().contains("startActivity")) {
				SootMethod method = mr.tryResolve();
				if (method != null) {
//					boolean result = calledByReceiver(method, jimpleBasedInterproceduralCFG, false, new HashSet<>());
//					System.out.println("cic");
				}
//					mr.getDeclaringClass().getMethods().forEach(sm -> {
//						if (sm.getName().contains("startActivity")) {
//							startActivities.add(sm.getSignature());
//						}
//					});
//					startActivities.add(mr.getSignature());
			}
		}
//		if (s.containsInvokeExpr()) {
//			SootMethodRef mr = s.getInvokeExpr().getMethodRef();
//			if (mr.getSignature().contains("startService") || mr.getSignature().contains("bindService")) {
//				mr.getDeclaringClass().getMethods().forEach(sm -> {
//					if (sm.getName().contains("startService") || sm.getName().contains("bindService")) {
//						startServices.add(sm.getSignature());
//					}
//				});
//				startServices.add(mr.getSignature());
//			}
//		}
//		if(unit.toString().contains("startService")) {
//			System.out.println("ci");
//		}
//		if (Arrays.stream(Activity.START_ACTIVITY).anyMatch(s -> unit.toString().contains(s))) {
//			System.out.println("ic");
//		}
//		if (unit.toString().contains(Service.START_SERVICE)) {
//			System.out.println("cic");
//		}
	}

	@Override
	public void processResults() {
	}

	@Override
	public void saveResults() {
	}

	@Override
	public void resultHandler(Object result) {

	}

	@Override
	public void onFinish() {
//		startActivities.forEach(s -> System.out.println("\"" + s + "\","));
//		startServices.forEach(s -> System.out.println("\"" + s + "\","));
	}
}

