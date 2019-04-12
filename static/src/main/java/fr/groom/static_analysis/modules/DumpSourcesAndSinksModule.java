package fr.groom.static_analysis.modules;

import fr.groom.static_analysis.StaticAnalysis;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider;
import soot.jimple.infoflow.sourcesSinks.definitions.MethodSourceSinkDefinition;
import soot.jimple.infoflow.sourcesSinks.definitions.SourceSinkDefinition;
import soot.jimple.internal.AbstractInvokeExpr;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DumpSourcesAndSinksModule extends Module<List<SourceSinkDefinition>> implements IModule {
	private ISourceSinkDefinitionProvider provider;
	private HashSet<String> sourceSignatures = new HashSet<>();
	private HashSet<String> sinkSignatures = new HashSet<>();

	public DumpSourcesAndSinksModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
		this.provider = staticAnalysis.getProvider();
		for (SourceSinkDefinition def : this.provider.getSources()) {
			MethodSourceSinkDefinition methodDef = (MethodSourceSinkDefinition) def;
			String signature = methodDef.getMethod().getSignature();
			this.sourceSignatures.add(signature);
		}
		for (SourceSinkDefinition def : this.staticAnalysis.getProvider().getSinks()) {
			MethodSourceSinkDefinition methodDef = (MethodSourceSinkDefinition) def;
			String signature = methodDef.getMethod().getSignature();
			this.sinkSignatures.add(signature);
		}
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		Stmt stmt = (Stmt) unit;
		if (stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			SootMethod invokedMethod = invokeExpr.getMethod();
			this.resultHandler(invokedMethod);
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
		if(invokedMethod == null)
			return;
		if(sourceSignatures.contains(invokedMethod.getSignature())) {
			this.staticAnalysis.addSource(invokedMethod.getSignature());
		}
		if(sinkSignatures.contains(invokedMethod.getSignature())) {
			this.staticAnalysis.addSink(invokedMethod.getSignature());
		}
	}

	@Override
	public void onFinish() {

	}
}
