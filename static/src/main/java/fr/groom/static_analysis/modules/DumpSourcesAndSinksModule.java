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

import java.util.ArrayList;
import java.util.List;

public class DumpSourcesAndSinksModule extends Module<List<SourceSinkDefinition>> implements IModule {
	private ISourceSinkDefinitionProvider provider;
	private List<String> sources = new ArrayList<>();
	private List<String> sinks = new ArrayList<>();

	public DumpSourcesAndSinksModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
		this.provider = staticAnalysis.getProvider();
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
		for (SourceSinkDefinition sourceDef : provider.getSources()) {
			if (sourceDef instanceof MethodSourceSinkDefinition) {
				MethodSourceSinkDefinition msd = (MethodSourceSinkDefinition) sourceDef;
				SootMethodAndClass smac = msd.getMethod();
				if (invokedMethod.getSignature().equals(smac.getSignature())) {
					this.staticAnalysis.addSource(invokedMethod.getSignature());
				}
			}
		}
		for (SourceSinkDefinition sinkDef : provider.getSinks()) {
			if (sinkDef instanceof MethodSourceSinkDefinition) {
				MethodSourceSinkDefinition msd = (MethodSourceSinkDefinition) sinkDef;
				SootMethodAndClass smac = msd.getMethod();
				if (invokedMethod.getSignature().equals(smac.getSignature())) {
					this.staticAnalysis.addSink(invokedMethod.getSignature());
				}
			}
		}
	}

	@Override
	public void onFinish() {

	}
}
