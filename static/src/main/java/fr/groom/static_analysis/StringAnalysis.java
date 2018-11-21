package fr.groom.static_analysis;

import fr.groom.models.Pair;
import fr.groom.models.TupleStringInt;
import fr.groom.Configuration;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.infoflow.android.resources.ARSCFileParser.AbstractResource;
import soot.jimple.infoflow.android.resources.ARSCFileParser.ResPackage;
import soot.jimple.infoflow.android.resources.ARSCFileParser.ResType;
import soot.jimple.infoflow.android.resources.ARSCFileParser.StringResource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StringAnalysis extends BackwardAnalysis implements IBackwardAnalysis {

	public final static String XML_STRINGS_FILE_PATH = "res/values/strings.xml";
	private final static String RESOURCES_SIGNATURE = "<android.content.Context: android.content.res.Resources getResources()>";
	private final static String APPEND_STRING_SIGNATURE = "<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>";
	private final static String APPEND_INT_SIGNATURE = "<java.lang.StringBuilder: java.lang.StringBuilder append(int)>";
	private final static String[] RESOURCE_METHOD_SIGNATURES = {
			"<android.support.v4.app.Fragment: java.lang.String getString(int)>",
			"<android.content.res.Resources: java.lang.String getString(int)>",
			"<android.app.Activity: java.lang.String getString(int)>",
			"<android.content.Context: java.lang.String getString(int)>"
	};
	private final static String[] PUT_STRING_INTENT_SIGNATURES = {
			"<android.os.Bundle: void putString(java.lang.String,java.lang.String)>",

	};

	public StringAnalysis(SootMethod sootMethod, Unit unit, Value value) {
		super(sootMethod, unit, value);
	}

	private StringAnalysis(SootMethod sootMethod, Unit unit, Value value, BackwardAnalysis parent) {
		super(sootMethod, unit, value, parent);
	}

	public static void computeResults(BackwardAnalysis node, List<String> results) {
		if (node.getValue() instanceof Constant) {
			for (String result : results) {
				results.set(results.indexOf(result), result + node.getValue().toString());
			}
		} else {
			for (BackwardAnalysis child : node.getChildren()) {
				computeResults(child, results);
			}
		}
	}

	public static List<String> getStringAnalysisResultsList(StringAnalysis stringAnalysis) {
		List<String> results = new ArrayList<>();
		results.add("");
		computeResults(stringAnalysis, results);
		return results;
	}

	public static String retrieveStringFromArsc(int stringID, String pathToApk) {
		ARSCFileParser arscFileParser = new ARSCFileParser();
		try {
			arscFileParser.parse(pathToApk);
			List<ResPackage> resPackages = arscFileParser.getPackages();
			for (ResPackage resPackage : resPackages) {
				List<ResType> resTypes = resPackage.getDeclaredTypes();
				for (ResType resType : resTypes) {
					Collection<AbstractResource> ressources = resType.getAllResources();
					AbstractResource test = resType.getFirstResource(stringID);
					if (test != null) {
						return ((StringResource) test).getValue();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void setValue(Value value) {
		this.value = value;
	}

	public void analyze() {
		if (this.getRoot().getTotalCount() < 100) {
			value.apply(new AbstractJimpleValueSwitch() {
				@Override
				public void caseLocal(Local v) {
					handleLocal(sootMethod, unit, v);
				}

				@Override
				public void caseParameterRef(ParameterRef v) {
					handleParameterRef(sootMethod, v);
				}

				@Override
				public void caseInstanceFieldRef(InstanceFieldRef v) {
					handleInstanceFieldRef(sootMethod, unit, v);
				}

				@Override
				public void caseStaticFieldRef(StaticFieldRef v) {
					handleStaticFieldRef(v);
				}

				@Override
				public void caseNewExpr(NewExpr v) {
					handleNewExpr(sootMethod, unit);
				}

				@Override
				public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
					handleVirtualInvokeExpr(sootMethod, unit, v);
				}

				@Override
				public void caseStaticInvokeExpr(StaticInvokeExpr v) {
					handleStaticInvokeExpr(unit, v);
				}

				@Override
				public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
					handleInterfaceInvokeExpr(sootMethod, unit, v);
				}

				@Override
				public void caseCastExpr(CastExpr v) {
					handleCastExpr(sootMethod, unit, v);
				}

				@Override
				public void defaultCase(Object v) {
					if (v instanceof Constant) {
					} else if (v instanceof InvokeExpr) {
						analyzeMethodInvocation(unit, (InvokeExpr) v);
					} else {
						handleDefault(sootMethod, unit, value);
					}
				}

			});
		}
	}

	private void handleVirtualInvokeExpr(SootMethod sootMethod, Unit unit, VirtualInvokeExpr virtualInvokeExpr) {
		if (isStringResourceMethod(virtualInvokeExpr.getMethod().getSignature())) {
			Value getStringArg = virtualInvokeExpr.getArg(0);
			String idString = getStringArg.toString();
			int id = Integer.parseInt(idString);
			String pathToApk = Configuration.v().getTargetApk();
			String result = retrieveStringFromArsc(id, pathToApk);
			if (result != null) {
				Value newValue = StringConstant.v(result);
				this.setValue(newValue);
			}
			// TODO : Handling Intent getStringExtra
//		} else if (virtualInvokeExpr.getMethod().getSignature().contains("getStringExtra")) {
//			Value storedKey = virtualInvokeExpr.getArg(0);
//			for (SootClass sootClass : Scene.v().getApplicationClasses()) {
//				for (SootMethod sootMethod2 : sootClass.getMethods()) {
//					if (sootMethod2.isConcrete()) {
//						for (Unit putUnit : sootMethod2.retrieveActiveBody().getUnits()) {
//								for (String putSignature : PUT_STRING_INTENT_SIGNATURES) {
//									if(putUnit.toString().contains(putSignature + "(" + storedKey)) {
//										System.out.printf("ici");
//										Stmt putStmt = (Stmt) putUnit;
//										if(putStmt instanceof InvokeStmt) {
//											InvokeExpr invokeExpr = ((Stmt) putUnit).getInvokeExpr();
//											StringAnalysis putStringAnalysis = new StringAnalysis(sootMethod2, putUnit,invokeExpr.getArg(1));
//											putStringAnalysis.analyze();
//										}
//									}
//							}
//						}
//					}
//				}
//			}
		} else {
			StringAnalysis baseAnalysis = new StringAnalysis(sootMethod, unit,
					virtualInvokeExpr.getBase(), this);
			baseAnalysis.analyze();
			for (Value arg : virtualInvokeExpr.getArgs()) {
				StringAnalysis concatAnalysis = new StringAnalysis(sootMethod, unit, arg, baseAnalysis);
				concatAnalysis.analyze();
			}
			analyzeMethodInvocation(unit, virtualInvokeExpr);
		}
	}

//	public TupleStringInt evaluateResults2() {
//		List<String> nullSring = new ArrayList<>();
//		nullSring.add("");
//		TupleStringInt result = new TupleStringInt(nullSring, 0);
//		if (this.getChildren().isEmpty()) {
//			if (this.getValue() instanceof Constant) {
//				List<String> strings = new ArrayList<>();
//				strings.add(this.getValue().toString());
//				result = new TupleStringInt(strings, 0);
//			}
//		} else {
//			ArrayList<TupleStringInt> childrenTuples = new ArrayList<>();
//			for (StringAnalysis child : this.getChildren()) {
//				TupleStringInt childTuple = child.evaluateResults2();
//				childrenTuples.add(childTuple);
//			}
//			result = TupleStringInt.merge(childrenTuples);
//
//		}
//		return result;
//	}

	private void handleStaticInvokeExpr(Unit unit, StaticInvokeExpr staticInvokeExpr) {
		analyzeMethodInvocation(unit, staticInvokeExpr);
	}

	private void handleInterfaceInvokeExpr(SootMethod sootMethod, Unit unit,
										   InterfaceInvokeExpr interfaceInvokeExpr) {
		StringAnalysis baseAnalysis = new StringAnalysis(sootMethod, unit,
				interfaceInvokeExpr.getBase(), this);
		baseAnalysis.analyze();
		for (Value arg : interfaceInvokeExpr.getArgs()) {
			StringAnalysis concatAnalysis = new StringAnalysis(sootMethod, unit, arg, baseAnalysis);
			concatAnalysis.analyze();
		}
		analyzeMethodInvocation(unit, interfaceInvokeExpr);
	}

	private boolean isStringResourceMethod(String signature) {
		for (String resourceSignature : RESOURCE_METHOD_SIGNATURES) {
			if (signature.equals(resourceSignature)) {
				return true;
			}
		}
		return false;
	}

	protected void startNewAnalysis(SootMethod sootMethod, Unit unit, Value value) {
		StringAnalysis analysis = new StringAnalysis(sootMethod, unit, value, this);
		analysis.analyze();
	}

	private void test(StringAnalysis node, List<Pair<String, Integer>> results) {
		if (node.getValue() instanceof StringConstant) {
			Pair<String, Integer> result = new Pair<>(node.getValue().toString(), node.getDepth());
			results.add(result);
		}
		if (node.hasChildren()) {
			for (BackwardAnalysis child : node.getChildren()) {
				if (child.getValue() instanceof StringConstant) {
					Pair<String, Integer> result = new Pair<>(child.getValue().toString(), child.getDepth());
					results.add(result);
				}
				test((StringAnalysis) child, results);
			}
		}
	}

	private HashMap<String, Integer> keepDeepestDistincts(List<Pair<String, Integer>> results) {
		HashMap<String, Integer> temp = new HashMap<>();
		for (Pair<String, Integer> result : results) {
			if (!temp.containsKey(result.getLeft())) {
				temp.put(result.getLeft(), result.getRight());
			} else {
				if (result.getRight() > temp.get(result.getLeft())) {
					temp.replace(result.getLeft(), temp.get(result.getLeft()), result.getRight());
				}
			}
		}
		return temp;
	}

	private TupleStringInt merge(HashMap<String, Integer> results) {
		Set<Map.Entry<String, Integer>> set = results.entrySet();
		ArrayList<TupleStringInt> tuples = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : set) {
			ArrayList<String> temp = new ArrayList<>();
			temp.add(entry.getKey());
			TupleStringInt t = new TupleStringInt(temp, entry.getValue());
			tuples.add(t);
		}
		List<String> nullSring = new ArrayList<>();
		nullSring.add("");
		TupleStringInt result = new TupleStringInt(nullSring, 0);
		tuples.add(result);
		return TupleStringInt.merge(tuples);
	}

	public TupleStringInt evaluateEasy() {
		List<Pair<String, Integer>> results = new ArrayList<>();
		test(this, results);
		HashMap<String, Integer> partialResults = keepDeepestDistincts(results);
		if (partialResults.size() > 5) {
			List<String> noMergeResults = new ArrayList<>();
			Set<Map.Entry<String, Integer>> set = partialResults.entrySet();
			for (Map.Entry<String, Integer> entry : set) {
				noMergeResults.add(entry.getKey());
			}
			return new TupleStringInt(noMergeResults, 0);
		} else {

			TupleStringInt merged = merge(partialResults);
			for (Map.Entry<String, Integer> entry : partialResults.entrySet()) {
				merged.getStrings().add(entry.getKey());
			}
			return merged;
		}
	}

	public TupleStringInt evaluateResults2() {

		List<String> nullSring = new ArrayList<>();
		nullSring.add("");
		TupleStringInt result = new TupleStringInt(nullSring, 0);
		if (this.getChildren().isEmpty()) {
			if (this.getValue() instanceof Constant) {
				List<String> strings = new ArrayList<>();
				strings.add(this.getValue().toString());
				result = new TupleStringInt(strings, 0);
			}
		} else {
			ArrayList<TupleStringInt> childrenTuples = new ArrayList<>();
			for (BackwardAnalysis child : this.getChildren()) {
				StringAnalysis stringAnalysis = (StringAnalysis) child;
				TupleStringInt childTuple = stringAnalysis.evaluateResults2();
				childrenTuples.add(childTuple);
			}
			result = TupleStringInt.merge(childrenTuples);
			result.setStrings(result.getStrings().stream().distinct().collect(Collectors.toList()));

		}
		return result;
	}
}
