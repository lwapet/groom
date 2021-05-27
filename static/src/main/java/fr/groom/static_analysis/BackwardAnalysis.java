package fr.groom.static_analysis;

//import com.google.common.collect.Lists;
import com.google.common.collect.Lists;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.scalar.*;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

abstract class BackwardAnalysis extends AbstractJimpleValueSwitch implements IBackwardAnalysis {
	protected SootMethod sootMethod;
	protected Unit unit;
	protected Value value;
	List<BackwardAnalysis> children = new ArrayList<>();
	private BackwardAnalysis parent;
	private BackwardAnalysis root;
	private int depth;
	private int totalCount;

	public BackwardAnalysis(SootMethod sootMethod, Unit unit, Value value) {
		this.sootMethod = sootMethod;
		this.unit = unit;
		this.value = value;
		this.depth = 0;
		this.root = this;
		this.totalCount = 0;
	}

	public BackwardAnalysis(SootMethod sootMethod, Unit unit, Value value, BackwardAnalysis parent) {
		this.sootMethod = sootMethod;
		this.unit = unit;
		this.value = value;
		this.parent = parent;
		this.root = parent.getRoot();
		this.parent.children.add(this);
		this.depth = parent.getDepth() + 1;
		this.getRoot().totalCount++;
	}

	private static void iterOverUnits(Chain<Unit> units, UnitHandler callback) {
		for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
			final Unit u = iter.next();
			callback.doWithUnit(u);
		}
	}

	private static Chain<Unit> listToChain(Collection<Unit> objects) {
		Chain<Unit> chain = new HashChain<>();
		chain.addAll(objects);
		return chain;
	}

	private static CombinedDUAnalysis getDefUseAnalysis(SootMethod sootMethod) {
		CompleteUnitGraph unitGraph = new CompleteUnitGraph(sootMethod.retrieveActiveBody());
		return new CombinedDUAnalysis(unitGraph);
	}

	private static SmartLocalDefs getDefs(SootMethod sootMethod) {
		CompleteUnitGraph unitGraph = new CompleteUnitGraph(sootMethod.retrieveActiveBody());
		LiveLocals liveLocals = new SimpleLiveLocals(unitGraph);
		return new SmartLocalDefs(unitGraph, liveLocals);
	}

	private static SimpleLocalUses getUses(SootMethod sootMethod) {
		CompleteUnitGraph unitGraph = new CompleteUnitGraph(sootMethod.retrieveActiveBody());
		SmartLocalDefs smartLocalDefs = getDefs(sootMethod);
		return new SimpleLocalUses(unitGraph, smartLocalDefs);
	}

	public static Chain<Unit> getUnitDefinitions(SootMethod sootMethod, Unit unit, Local local) {
		List<Unit> defUnits = new ArrayList<>();
		if (sootMethod.isConcrete()) {
			SmartLocalDefs smartLocalDefs = getDefs(sootMethod);
			defUnits = smartLocalDefs.getDefsOfAt(local, unit);
//			defUnits = getDefUseAnalysis(sootMethod).getDefsOfAt(local, unit);
		}
		return listToChain(defUnits);
	}

	private static List<UnitValueBoxPair> getUnitUses(SootMethod sootMethod, Unit unit) {
		if (sootMethod.isConcrete()) {
			SimpleLocalUses simpleLocalUses = getUses(sootMethod);
			return simpleLocalUses.getUsesOf(unit);
//			return getDefUseAnalysis(sootMethod).getUsesOf(unit);
		}
		return new ArrayList<>();
	}

	protected static Chain<Unit> getReturnUnits(SootMethod sootMethod) {
		Chain<Unit> returnedUnits = new HashChain<>();
		if (sootMethod.isConcrete()) {
			Body body = sootMethod.retrieveActiveBody();
			iterOverUnits(body.getUnits(), unit -> {
				if (unit instanceof ReturnStmt) returnedUnits.add(unit);
			});
		}
		return returnedUnits;
	}

	public SootMethod getSootMethod() {
		return sootMethod;
	}

	public Unit getUnit() {
		return unit;
	}

	public Value getValue() {
		return value;
	}

	public List<BackwardAnalysis> getChildren() {
		return children;
	}

	public BackwardAnalysis getParent() {
		return parent;
	}

	public int getDepth() {
		return depth;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	protected void startNewAnalysis(SootMethod sootMethod, Unit unit, Value value) {
	}

	private void analyze(SootMethod sootMethod, Unit unit, Value value) {
	}

	protected BackwardAnalysis getRoot() {
		return this.root;
	}

	protected void getHistory(List<String> history) {
		history.add(this.unit.toString());
		if (this.hasParent()) {
			this.getParent().getHistory(history);
		}
	}

	void handleLocal(SootMethod sootMethod, Unit unit, Local local) {
		Chain<Unit> defUnits = getUnitDefinitions(sootMethod, unit, local);
		for (Unit defUnit : defUnits) {
			if (defUnit instanceof AssignStmt) {
				AssignStmt assignStmt = (AssignStmt) defUnit;
				startNewAnalysis(sootMethod, defUnit, assignStmt.getRightOp());
			} else if (defUnit instanceof IdentityStmt) {
				IdentityStmt identityStmt = (IdentityStmt) defUnit;
				startNewAnalysis(sootMethod, defUnit, identityStmt.getRightOp());
			}
		}
	}

	void handleDefault(SootMethod sootMethod, Unit unit, Value value) {
		System.out.println("case to handle " + value.getType().toString());
	}

	void handleParameterRef(SootMethod sootMethod, ParameterRef parameterRef) {
		interproceduralAnalysis(sootMethod, parameterRef);
	}

	void handleStaticFieldRef(StaticFieldRef staticFieldRef) {
		searchForFieldUses(staticFieldRef.getField());
	}

	void handleInstanceFieldRef(SootMethod sootMethod, Unit unit, InstanceFieldRef instanceFieldRef) {
		startNewAnalysis(sootMethod, unit, instanceFieldRef.getBase());
		searchForFieldUses(instanceFieldRef.getField());
	}

	void handleNewExpr(SootMethod sootMethod, Unit unit) {
		searchForNewExprUse(sootMethod, unit);
	}

	void handleCastExpr(SootMethod sootMethod, Unit unit, CastExpr castExpr) {
		startNewAnalysis(sootMethod, unit, castExpr.getOp());
	}

	public void returnValueAnalysis(SootMethod sootMethod) {
		Chain<Unit> returnUnits = getReturnUnits(sootMethod);
		iterOverUnits(returnUnits, unit -> {
			ReturnStmt returnStmt = (ReturnStmt) unit;
			startNewAnalysis(sootMethod, unit, returnStmt.getOp());
		});
	}

	protected void analyzeMethodInvocation(Unit unit, InvokeExpr invokeExpr) {
		// Analyze the method return value
		returnValueAnalysis(invokeExpr.getMethod());
		// analyze the value of each method invocation argument
		for (Value argument : invokeExpr.getArgs()) {
			this.startNewAnalysis(sootMethod, unit, argument);
		}
	}


	private void interproceduralAnalysis(SootMethod sootMethod, ParameterRef parameterRef) {
		JimpleBasedInterproceduralCFG jimpleBasedInterproceduralCFG = new JimpleBasedInterproceduralCFG();
		Collection<Unit> callers = jimpleBasedInterproceduralCFG.getCallersOf(sootMethod);
		Chain<Unit> callersChain = listToChain(callers);
		iterOverUnits(callersChain, caller -> {
			SootMethod callerMethod = jimpleBasedInterproceduralCFG.getMethodOf(caller);
			if (callerMethod != null) {
				if (caller instanceof InvokeStmt) {
					InvokeStmt invokeStmt = (InvokeStmt) caller;
					Value wantedArg = invokeStmt.getInvokeExpr().getArg(parameterRef.getIndex());
					startNewAnalysis(callerMethod, caller, wantedArg);
				} else if (caller instanceof AssignStmt) {
					AssignStmt assignStmt = (AssignStmt) caller;
					Value rightOp = assignStmt.getRightOp();
					if (rightOp instanceof InvokeExpr) {
						InvokeExpr invokeExpr = (InvokeExpr) rightOp;
						Value wantedArg = invokeExpr.getArg(parameterRef.getIndex());
						this.startNewAnalysis(callerMethod, caller, wantedArg);
					}
				}
			}
		});
	}

	private void searchForNewExprUse(SootMethod method, Unit unit) {
		// TODO aller voir dans les constructeurs.
		List<UnitValueBoxPair> uses = getUnitUses(method, unit);
		uses = Lists.reverse(uses);
		for (UnitValueBoxPair useBox : uses) {

			Unit use = useBox.getUnit();
			int line = use.getJavaSourceStartLineNumber();
			Value test = useBox.getValueBox().getValue();
			//todo check value of test, interesting or not ?
			// if not, use iterOverUnits.
			Stmt stmt = (Stmt) use;
			if (stmt.containsInvokeExpr()) {
				analyzeMethodInvocation(use, stmt.getInvokeExpr());
			}
		}
	}

	private boolean checkUnitHistory(Unit unit) {
		if (this.unit.toString().equals(unit.toString())) {
			return true;
		}
		while (this.hasParent()) {
			return this.getParent().checkUnitHistory(unit);
		}
		return false;
	}

	private void searchForFieldUses(SootField sootField) {
		SootClass sootClass = sootField.getDeclaringClass();
		Iterator<SootMethod> sootMethodIterator = sootClass.methodIterator();
		while (sootMethodIterator.hasNext()) {
			// Todo check history
			SootMethod classMethod = sootMethodIterator.next();
			if (classMethod.isConcrete()) {
				iterOverUnits(classMethod.retrieveActiveBody().getUnits(), classMethodUnit -> {
					if (classMethodUnit instanceof AssignStmt) {
						Value rightOp = ((AssignStmt) classMethodUnit).getRightOp();
						Value leftOp = ((AssignStmt) classMethodUnit).getLeftOp();
						if (rightOp.toString().contains(sootField.getSignature())) {
							List<UnitValueBoxPair> uses = getUnitUses(classMethod, classMethodUnit);
							for (UnitValueBoxPair use : uses) {
								boolean alreadyAnalyzed = checkUnitHistory(use.getUnit());
								if (!alreadyAnalyzed) {
									if (use.getUnit() instanceof InvokeStmt) {
										InvokeStmt stmt = (InvokeStmt) use.getUnit();
										analyzeMethodInvocation(use.getUnit(), stmt.getInvokeExpr());
									}
								}
							}
						} else if (leftOp.toString().contains(sootField.getSignature())) {
							startNewAnalysis(classMethod, classMethodUnit, rightOp);
						}
					}
				});
			}
		}
	}
}
