package fr.groom.static_analysis.modules;

import fr.groom.static_analysis.StaticAnalysis;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.List;

public class DumpRegisterReceiverSpots extends Module<List<String>> implements  IModule{
	public DumpRegisterReceiverSpots(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		Stmt stmt = (Stmt) unit;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			SootMethod invokedMethod = invokeExpr.getMethod();
			if(invokedMethod.getName().contains("registerReceiver")) {
				System.out.println("ici");
			}
//			this.resultHandler(invokedMethod);
		}
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {

	}

	@Override
	public void resultHandler(Object result) {
		SootMethod invokedMethod = (SootMethod) result;
		if(invokedMethod.getName().contains("registerReceiver")) {
			System.out.println("ici");
		}
	}

	@Override
	public void onFinish() {

	}
}
