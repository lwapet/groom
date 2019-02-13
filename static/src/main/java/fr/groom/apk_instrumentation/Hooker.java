package fr.groom.apk_instrumentation;

import fr.groom.models.*;
import soot.*;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.*;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.*;

public class Hooker {
	private Application app;
	private SootInstrumenter sootInstrumenter;
	private ArrayList<Unit> hookedUnits;
	private ArrayList<SootMethod> hookedMethods;
	static String INTENT_CLASS_NAME = "android.content.Intent";
	static String STRING_CLASS_NAME = "java.lang.String";
	static String OBJECT_CLASS_NAME = "java.lang.Object";
	public static Type STRING_TYPE = RefType.v(STRING_CLASS_NAME);
	public static Type INTENT_TYPE = RefType.v(INTENT_CLASS_NAME);
	public static Type OBJECT_TYPE = RefType.v(OBJECT_CLASS_NAME);

	//	private static String[] activityCallbacks = {"onCreate", "onStart", "onResume", "onRestart", "onDestroy", "onStop", "onPause"};


	private HashSet<SootMethod> ACTIVITY_CALLBACKS;
	private HashSet<SootMethod> SERVICE_CALLBACKS;
	private HashSet<SootMethod> RECEIVER_CALLBACKS;
	private static SootClass GROOM = Scene.v().getSootClass(InstrumenterUtils.groomClassName);
	private static SootClass FAKE_ACTIVITY = Scene.v().getSootClass("fakeActivity");
	//	private static String INTENT_LOG_GROOM_METHOD = "logIntent";
//	private static String METHOD_LOG_GROOM_METHOD = "logMethod";
	private static String REFLECTION_LOG_GROOM_METHOD = "logReflectionInvoke";
	//	private static String STATEMENT_LOG_GROOM_METHOD = "logStatement";
	private static String GROOM_LOG_METHOD = "log";

	public Hooker(Application app, SootInstrumenter sootInstrumenter) {
		this.app = app;
		this.sootInstrumenter = sootInstrumenter;
		this.ACTIVITY_CALLBACKS = new HashSet<>();

		this.app.getComponents().forEach(IComponent::addCallbacks);

		SootMethod groomStaticInitializer = GROOM.getMethodByName(SootMethod.staticInitializerName);
		SootClass klass = Scene.v().getSootClass("java.lang.Class");
		SootMethod forName = klass.getMethod("forName", Collections.singletonList(STRING_TYPE));
		ArrayList<Value> args = new ArrayList<>();
		args.add(StringConstant.v(groomStaticInitializer.getName()));
		StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(forName.makeRef(), args);
		InvokeStmt injectedStmt = Jimple.v().newInvokeStmt(staticInvokeExpr);


	}

	public void hookCallbacks() {
		this.app.getComponents().forEach(this::hookComponentCallbacks);
	}

	void hook(SootMethod sootMethod, Unit unit, InvokeExpr invokeExpr) {
		List<Unit> unitsToInject = new ArrayList<>();
		List<Value> groomLogArguments = new ArrayList<>();
		Stmt stmt = (Stmt) unit;
		Value intentValue = invokeExpr.getArgs().stream().filter(a -> a.getType().equals(INTENT_TYPE)).findFirst().orElse(null);
		if (intentValue == null)
			return;
		groomLogArguments.add(StringConstant.v(invokeExpr.getMethod().getSignature()));
		groomLogArguments.add(intentValue);
		Body body = sootMethod.retrieveActiveBody();
		Value thisValue;
		if (!sootMethod.isStatic()) {
			thisValue = body.getThisLocal();
		} else {
			thisValue = NullConstant.v();
		}
		groomLogArguments.add(thisValue);
		SootMethod groomLog = GROOM.getMethodByName(GROOM_LOG_METHOD);
		InvokeExpr groomLogExpr = Jimple.v().newStaticInvokeExpr(groomLog.makeRef(), groomLogArguments);
		InvokeStmt groomLogStmt = Jimple.v().newInvokeStmt(groomLogExpr);
		unitsToInject.add(groomLogStmt);
		injectStatements(sootMethod, unitsToInject, unit);
		sootMethod.getActiveBody().validate();
		System.out.println("statement hooked : " + unit.toString());
	}

	void hookUnit(SootMethod sootMethod, Unit unit) {
		List<Value> args = new ArrayList<>();
		Stmt stmt = (Stmt) unit;
		if (!stmt.containsInvokeExpr())
			return;
		InvokeExpr unitInvokeExpr = stmt.getInvokeExpr();
		Value intentValue = unitInvokeExpr.getArgs().stream().filter(a -> a.getType().equals(INTENT_TYPE)).findFirst().orElse(null);
		if (intentValue == null)
			return;
		Body body = sootMethod.retrieveActiveBody();
		Value thisValue;
		if (!sootMethod.isStatic()) {
			thisValue = body.getThisLocal();
		} else {
			thisValue = NullConstant.v();
		}
		SootMethod groomLogIntent = GROOM.getMethodByName("logIntent");
		InvokeExpr injectedExpr = Jimple.v().newStaticInvokeExpr(groomLogIntent.makeRef(), args);
		InvokeStmt inkectedStmt = Jimple.v().newInvokeStmt(injectedExpr);
		body.getUnits().insertBefore(inkectedStmt, unit);
		body.validate();

		System.out.println("statement hooked : " + unit.toString());
	}

	private boolean isBefore(PatchingChain<Unit> units, Unit unit1, Unit unit2) {
		Iterator<Unit> unitIterator = units.snapshotIterator();
		while (unitIterator.hasNext()) {
			Unit currentUnit = unitIterator.next();
			if (currentUnit.equals(unit1)) {
				return true;
			}
			if (currentUnit.equals(unit2)) {
				return false;
			}
		}
		return false;
	}

	private Unit insertPosition(SootMethod methodToInstrument, Unit unitToInstrument) {
		Body bodyToInstrument = methodToInstrument.getActiveBody();
		CompleteUnitGraph unitGraph = new CompleteUnitGraph(bodyToInstrument);
		SimpleLocalDefs defs = new SimpleLocalDefs(unitGraph);
		Unit thisDefUnit = null;
		if (!methodToInstrument.isStatic()) {
			List<Unit> thisLocalDefs = defs.getDefsOf(bodyToInstrument.getThisLocal());
			if (!thisLocalDefs.isEmpty()) {
				thisDefUnit = thisLocalDefs.get(0);
			}
		}
		Unit lastParamDefUnit = null;
		Unit superCallUnit = getSuperCall(methodToInstrument);
		if (methodToInstrument.getParameterCount() != 0) {
			Local lastParamLocal = bodyToInstrument.getParameterLocal(methodToInstrument.getParameterCount() - 1);
			List<Unit> lastParamDefs = defs.getDefsOf(lastParamLocal);
			lastParamDefUnit = lastParamDefs.get(0);
		}
		if (superCallUnit != null) {
			if (unitToInstrument != null && !isBefore(bodyToInstrument.getUnits(), unitToInstrument, superCallUnit))
				return unitToInstrument;
			return bodyToInstrument.getUnits().getSuccOf(superCallUnit);
		} else {
			if (unitToInstrument != null)
				return unitToInstrument;
			if (lastParamDefUnit != null) {
				return bodyToInstrument.getUnits().getSuccOf(lastParamDefUnit);
			} else {
				if (thisDefUnit != null) {
					return bodyToInstrument.getUnits().getSuccOf(thisDefUnit);
				} else {
					return bodyToInstrument.getUnits().getFirst();
				}
			}
		}
	}

	void injectStatement(SootMethod methodToInstrument, Unit unitToInstrument, Stmt statementToInject) {
		Body bodyToInstrument = methodToInstrument.getActiveBody();
		CompleteUnitGraph unitGraph = new CompleteUnitGraph(bodyToInstrument);
		SimpleLocalDefs defs = new SimpleLocalDefs(unitGraph);
		Unit thisDefUnit = defs.getDefsOf(bodyToInstrument.getThisLocal()).get(0);
		Unit lastParamDefUnit = null;
		Unit superCallUnit = getSuperCall(methodToInstrument);
		if (methodToInstrument.getParameterCount() != 0) {
			Local lastParamLocal = bodyToInstrument.getParameterLocal(methodToInstrument.getParameterCount() - 1);
			List<Unit> lastParamDefs = defs.getDefsOf(lastParamLocal);
			lastParamDefUnit = lastParamDefs.get(0);
		}
		if (superCallUnit != null) {
			if (unitToInstrument != null) {
				if (isBefore(bodyToInstrument.getUnits(), unitToInstrument, superCallUnit)) {
					bodyToInstrument.getUnits().insertAfter(statementToInject, superCallUnit);
				} else {
					bodyToInstrument.getUnits().insertAfter(statementToInject, unitToInstrument);
				}
			} else {
				bodyToInstrument.getUnits().insertAfter(statementToInject, superCallUnit);
			}
		} else {
			if (lastParamDefUnit != null) {
				bodyToInstrument.getUnits().insertAfter(statementToInject, lastParamDefUnit);
			} else {
				if (thisDefUnit != null) {
					bodyToInstrument.getUnits().insertAfter(statementToInject, thisDefUnit);
				} else {
					bodyToInstrument.getUnits().addFirst(statementToInject);
				}
			}
		}
	}

	Unit getSuperCall(SootMethod constructorMethod) {
		if (constructorMethod.isStatic() || !constructorMethod.isConstructor()) {
			return null;
		}
		if (!constructorMethod.hasActiveBody())
			constructorMethod.retrieveActiveBody();

		Unit[] superCall = {null};
		Body constructorBody = constructorMethod.getActiveBody();
		constructorBody.getUnits().forEach(unit -> unit.apply(new AbstractStmtSwitch() {
			@Override
			public void caseInvokeStmt(InvokeStmt stmt) {
				stmt.getInvokeExpr().apply(new AbstractExprSwitch() {
					@Override
					public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
						SootClass superClass = constructorMethod.getDeclaringClass().getSuperclass();
						if (v.getBase().equals(constructorBody.getThisLocal()) && v.getMethod().getDeclaringClass().equals(superClass) && v.getMethod().isConstructor()) {
							superCall[0] = unit;
						}
					}
				});
			}
		}));
		return superCall[0];
	}

	void injectStatements(SootMethod sootMethod, List<Unit> unitsToInsert, Unit unitToInstrument) {
		Unit insertPosition = insertPosition(sootMethod, unitToInstrument);
		sootMethod.getActiveBody().getUnits().insertBefore(unitsToInsert, insertPosition);
	}

	void hookComponentCallbacks(IComponent component) {
		component.getCallbacks().forEach(callback -> {
			List<Unit> unitsToInject = new ArrayList<>();
			List<Value> groomLogArguments = new ArrayList<>();
			Body body = callback.getSootMethod().retrieveActiveBody();
//			groomLogArguments.add(StringConstant.v(callback.getSootMethod().getSignature()));
			SootClass arrayListClass = Scene.v().getSootClass("java.util.ArrayList");
			NewExpr newArrayListExpr = Jimple.v().newNewExpr(RefType.v(arrayListClass));
			LocalGenerator localGenerator = new LocalGenerator(body);
			Local arrayRefLocal = localGenerator.generateLocal(newArrayListExpr.getType());
			AssignStmt newArrayAssignStmt = Jimple.v().newAssignStmt(arrayRefLocal, newArrayListExpr);
			unitsToInject.add(newArrayAssignStmt);

			SootMethod arrayListInit = arrayListClass.getMethod(SootMethod.constructorName, Collections.emptyList());
			SpecialInvokeExpr initExpr = Jimple.v().newSpecialInvokeExpr(arrayRefLocal, arrayListInit.makeRef());
			unitsToInject.add(Jimple.v().newInvokeStmt(initExpr));

			List<Type> arrayListAddArguments = new ArrayList<>();
			arrayListAddArguments.add(OBJECT_TYPE);
			SootMethod arrayListAdd = arrayListClass.getMethod("add", arrayListAddArguments);

			component.apply(new AbstractComponentSwitch() {
				@Override
				public void caseActivity(Activity activity) {
					Local thisLocal = body.getThisLocal();
					LocalGenerator localGenerator = new LocalGenerator(body);
					Local intentLocal = localGenerator.generateLocal(INTENT_TYPE);
					SootClass activityClass = Scene.v().getSootClass(Activity.SUPER_CLASS_NAME);
					SootMethod getIntent = activityClass.getMethod("getIntent", Collections.emptyList(), INTENT_TYPE);
					InvokeExpr invokeExpr1 = Jimple.v().newVirtualInvokeExpr(thisLocal, getIntent.makeRef());
					AssignStmt assignStmt = Jimple.v().newAssignStmt(intentLocal, invokeExpr1);
					unitsToInject.add(assignStmt);
					VirtualInvokeExpr virtualInvokeExpr = Jimple.v().newVirtualInvokeExpr(arrayRefLocal, arrayListAdd.makeRef(), Collections.singletonList(intentLocal));
					InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(virtualInvokeExpr);
					unitsToInject.add(invokeStmt);
				}

//				@Override
//				public void defaultCase(Object object) {
//					int intentParameterIndex = -1;
//					for (int i = 0; i < callback.getSootMethod().getParameterCount(); i++) {
//						Type parameterType = callback.getSootMethod().getParameterType(i);
//						if (parameterType.equals(INTENT_TYPE)) {
//							intentParameterIndex = i;
//						}
//					}
//					if (intentParameterIndex != -1) {
//						Local intentLocal = body.getParameterLocal(intentParameterIndex);
//						groomLogArguments.add(intentLocal);
//					} else {
//						groomLogArguments.add(NullConstant.v());
//					}
//				}
			});
			for (int i = 0; i < callback.getSootMethod().getParameterCount(); i++) {
				Local local = body.getParameterLocal(i);
//				CastExpr castExpr = Jimple.v().newCastExpr(local, OBJECT_TYPE);
				Local localToPush = local;
				if (local.getType() instanceof PrimType) {
					PrimType primType = (PrimType) local.getType();
					localToPush = localGenerator.generateLocal(primType.boxedType());
					SootClass boxedClass = Scene.v().getSootClass(primType.boxedType().getClassName());
					SootMethod boxedInit = boxedClass.getMethod("valueOf", Collections.singletonList(primType));
					StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(boxedInit.makeRef(), Collections.singletonList(local));
					AssignStmt assignStmt = Jimple.v().newAssignStmt(localToPush, staticInvokeExpr);
					unitsToInject.add(assignStmt);
				}
				VirtualInvokeExpr virtualInvokeExpr = Jimple.v().newVirtualInvokeExpr(arrayRefLocal, arrayListAdd.makeRef(), Collections.singletonList(localToPush));
				InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(virtualInvokeExpr);
				unitsToInject.add(invokeStmt);
			}

//			groomLogArguments.add(arrayRefLocal);

			SootClass hashMapClass = Scene.v().getSootClass("java.util.HashMap");
			NewExpr newHashMapExpr = Jimple.v().newNewExpr(RefType.v(hashMapClass));
			Local hashMapRefLocal = localGenerator.generateLocal(newHashMapExpr.getType());
			AssignStmt newHashMapAssignStmt = Jimple.v().newAssignStmt(hashMapRefLocal, newHashMapExpr);
			unitsToInject.add(newHashMapAssignStmt);

			SootMethod hashMapInit = hashMapClass.getMethod(SootMethod.constructorName, Collections.EMPTY_LIST);
			SpecialInvokeExpr hashMapInitExpr = Jimple.v().newSpecialInvokeExpr(hashMapRefLocal, hashMapInit.makeRef());
			unitsToInject.add(Jimple.v().newInvokeStmt(hashMapInitExpr));

			List<Type> hashMapAddArguments = new ArrayList<>();
			hashMapAddArguments.add(OBJECT_TYPE);
			hashMapAddArguments.add(OBJECT_TYPE);
			SootMethod hashMapPut = hashMapClass.getMethod("put", hashMapAddArguments);

			HashMap<String, String> data = new HashMap<>();
			data.put("signature", callback.getSootMethod().getSignature());
			data.put("type", "component_callback");
			data.forEach((s, v) -> {
				List<Value> hashMapArgs = new ArrayList<>();
				hashMapArgs.add(StringConstant.v(s));
				hashMapArgs.add(StringConstant.v(v));

				VirtualInvokeExpr virtualInvokeExpr = Jimple.v().newVirtualInvokeExpr(hashMapRefLocal, hashMapPut.makeRef(), hashMapArgs);
				InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(virtualInvokeExpr);
				unitsToInject.add(invokeStmt);
			});

			groomLogArguments.add(hashMapRefLocal);
			groomLogArguments.add(arrayRefLocal);

			Local thisLocal = body.getThisLocal();
			groomLogArguments.add(thisLocal);

			SootMethod groomLog = GROOM.getMethodByName(GROOM_LOG_METHOD);
			InvokeExpr groomLogExpr = Jimple.v().newStaticInvokeExpr(groomLog.makeRef(), groomLogArguments);
			InvokeStmt groomLogStmt = Jimple.v().newInvokeStmt(groomLogExpr);
			unitsToInject.add(groomLogStmt);
			injectStatements(callback.getSootMethod(), unitsToInject, null);
			callback.getSootMethod().getActiveBody().validate();
			System.out.println("Component callback hooked: " + callback.getSootMethod().getSignature());
			this.sootInstrumenter.injectedUnits.addAll(unitsToInject);
			this.sootInstrumenter.injectedUnitsCount += unitsToInject.size();
			this.sootInstrumenter.statementHookedCount += 1;
		});
	}


	private static Set<Class<?>> getWrapperTypes() {
		Set<Class<?>> ret = new HashSet<Class<?>>();
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(Void.class);
		return ret;
	}


	void hookReflectionMethod(SootMethod sootMethod, Unit unit, InvokeExpr invokeExpr) {
		if (!sootMethod.hasActiveBody()) {
			sootMethod.retrieveActiveBody();
		}
		List<Unit> unitsToInject = new ArrayList<>();
		List<Value> groomLogArguments = new ArrayList<>();

		VirtualInvokeExpr virtualInvokeExpr = (VirtualInvokeExpr) invokeExpr;
		Value base = virtualInvokeExpr.getBase();
		Value declaringClass = virtualInvokeExpr.getArg(0);
		Value reflectArgs = virtualInvokeExpr.getArg(1);
		groomLogArguments.add(base);
		groomLogArguments.add(declaringClass);
		groomLogArguments.add(reflectArgs);
		SootMethod groomLog = GROOM.getMethodByName(REFLECTION_LOG_GROOM_METHOD);
		InvokeExpr groomLogExpr = Jimple.v().newStaticInvokeExpr(groomLog.makeRef(), groomLogArguments);
		InvokeStmt groomLogStmt = Jimple.v().newInvokeStmt(groomLogExpr);
		unitsToInject.add(groomLogStmt);
		injectStatements(sootMethod, unitsToInject, unit);
		sootMethod.getActiveBody().validate();
	}

//	void hookMethod(SootMethod sootMethod, Unit unit, InvokeExpr invokeExpr) {
//		if (!sootMethod.hasActiveBody()) {
//			sootMethod.retrieveActiveBody();
//		}
//		List<Unit> unitsToInject = new ArrayList<>();
//		List<Value> groomLogArguments = new ArrayList<>();
//		groomLogArguments.add(NullConstant.v());
//		groomLogArguments.add(StringConstant.v(invokeExpr.getMethod().getSignature()));
//		groomLogArguments.add(NullConstant.v());
//		SootMethod groomLog = GROOM.getMethodByName(GROOM_LOG_METHOD);
//		InvokeExpr groomLogExpr = Jimple.v().newStaticInvokeExpr(groomLog.makeRef(), groomLogArguments);
//		InvokeStmt groomLogStmt = Jimple.v().newInvokeStmt(groomLogExpr);
//		unitsToInject.add(groomLogStmt);
//		injectStatements(sootMethod, unitsToInject, unit);
//		sootMethod.getActiveBody().validate();
//		System.out.println("method hooked" + invokeExpr.getMethod().getSignature());
//	}

	public void hookInvokeExpr(SootMethod sootMethod, Unit unit, InvokeExpr invokeExpr, String information) {
		if (!sootMethod.hasActiveBody()) {
			sootMethod.retrieveActiveBody();
		}
		SootClass arrayListClass = Scene.v().getSootClass("java.util.ArrayList");
		List<Unit> unitsToInject = new ArrayList<>();
		List<Value> groomLogArguments = new ArrayList<>();
		Body body = sootMethod.getActiveBody();

		NewExpr newArrayListExpr = Jimple.v().newNewExpr(RefType.v(arrayListClass));
		LocalGenerator localGenerator = new LocalGenerator(body);
		Local arrayRefLocal = localGenerator.generateLocal(newArrayListExpr.getType());
		AssignStmt newArrayAssignStmt = Jimple.v().newAssignStmt(arrayRefLocal, newArrayListExpr);
		unitsToInject.add(newArrayAssignStmt);

		SootMethod arrayListInit = arrayListClass.getMethod(SootMethod.constructorName, Collections.emptyList());
		SpecialInvokeExpr initExpr = Jimple.v().newSpecialInvokeExpr(arrayRefLocal, arrayListInit.makeRef());
		unitsToInject.add(Jimple.v().newInvokeStmt(initExpr));

		List<Type> arrayListAddArguments = new ArrayList<>();
		arrayListAddArguments.add(OBJECT_TYPE);
		SootMethod arrayListAdd = arrayListClass.getMethod("add", arrayListAddArguments);
		List<Value> methodArguments;
		for (Value arg : invokeExpr.getArgs()) {
//				CastExpr castExpr = Jimple.v().newCastExpr(local, OBJECT_TYPE);
			Value localToPush = arg;
			if (arg.getType() instanceof PrimType) {
				PrimType primType = (PrimType) arg.getType();
				localToPush = localGenerator.generateLocal(primType.boxedType());
				SootClass boxedClass = Scene.v().getSootClass(primType.boxedType().getClassName());
				SootMethod boxedInit = boxedClass.getMethod("valueOf", Collections.singletonList(primType));
				StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(boxedInit.makeRef(), Collections.singletonList(arg));
				AssignStmt assignStmt = Jimple.v().newAssignStmt(localToPush, staticInvokeExpr);
				unitsToInject.add(assignStmt);
			}

			VirtualInvokeExpr virtualInvokeExpr = Jimple.v().newVirtualInvokeExpr(arrayRefLocal, arrayListAdd.makeRef(), Collections.singletonList(localToPush));
			InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(virtualInvokeExpr);
			unitsToInject.add(invokeStmt);
		}
		groomLogArguments.add(StringConstant.v(invokeExpr.getMethod().getSignature()));
		groomLogArguments.add(StringConstant.v(information));
		groomLogArguments.add(arrayRefLocal);
		Value thisValue;
		if (!sootMethod.isStatic()) {
			thisValue = body.getThisLocal();
			if (invokeExpr.getMethod().isConstructor() && invokeExpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
				Value base = instanceInvokeExpr.getBase();
				if (instanceInvokeExpr.getMethod().isConstructor() && base.equals(thisValue)) {
					thisValue = NullConstant.v();
				}
			}
		} else {
			thisValue = NullConstant.v();
		}
		groomLogArguments.add(thisValue);

		SootMethod groomLog = GROOM.getMethodByName(GROOM_LOG_METHOD);
		InvokeExpr groomLogExpr = Jimple.v().newStaticInvokeExpr(groomLog.makeRef(), groomLogArguments);
		InvokeStmt groomLogStmt = Jimple.v().newInvokeStmt(groomLogExpr);
		unitsToInject.add(groomLogStmt);
		injectStatements(sootMethod, unitsToInject, unit);
		sootMethod.getActiveBody().validate();

		System.out.println("Statement hooked: " + unit.toString());
		this.sootInstrumenter.statementHookedCount += 1;
	}

	public void hookExpression(SootMethod sootMethod, Unit unit, InvokeExpr invokeExpr, HashMap<String, String> data) {
		if (!sootMethod.hasActiveBody()) {
			sootMethod.retrieveActiveBody();
		}
		SootClass arrayListClass = Scene.v().getSootClass("java.util.ArrayList");
		List<Unit> unitsToInject = new ArrayList<>();
		List<Value> groomLogArguments = new ArrayList<>();
		Body body = sootMethod.getActiveBody();


		NewExpr newArrayListExpr = Jimple.v().newNewExpr(RefType.v(arrayListClass));
		LocalGenerator localGenerator = new LocalGenerator(body);
		Local arrayRefLocal = localGenerator.generateLocal(newArrayListExpr.getType());
		AssignStmt newArrayAssignStmt = Jimple.v().newAssignStmt(arrayRefLocal, newArrayListExpr);
		unitsToInject.add(newArrayAssignStmt);

		SootMethod arrayListInit = arrayListClass.getMethod(SootMethod.constructorName, Collections.emptyList());
		SpecialInvokeExpr initExpr = Jimple.v().newSpecialInvokeExpr(arrayRefLocal, arrayListInit.makeRef());
		unitsToInject.add(Jimple.v().newInvokeStmt(initExpr));

		List<Type> arrayListAddArguments = new ArrayList<>();
		arrayListAddArguments.add(OBJECT_TYPE);
		SootMethod arrayListAdd = arrayListClass.getMethod("add", arrayListAddArguments);
		List<Value> methodArguments;
		for (Value arg : invokeExpr.getArgs()) {
//				CastExpr castExpr = Jimple.v().newCastExpr(local, OBJECT_TYPE);
			Value localToPush = arg;
			if (arg.getType() instanceof PrimType) {
				PrimType primType = (PrimType) arg.getType();
				localToPush = localGenerator.generateLocal(primType.boxedType());
				SootClass boxedClass = Scene.v().getSootClass(primType.boxedType().getClassName());
				SootMethod boxedInit = boxedClass.getMethod("valueOf", Collections.singletonList(primType));
				StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(boxedInit.makeRef(), Collections.singletonList(arg));
				AssignStmt assignStmt = Jimple.v().newAssignStmt(localToPush, staticInvokeExpr);
				unitsToInject.add(assignStmt);
			}

			VirtualInvokeExpr virtualInvokeExpr = Jimple.v().newVirtualInvokeExpr(arrayRefLocal, arrayListAdd.makeRef(), Collections.singletonList(localToPush));
			InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(virtualInvokeExpr);
			unitsToInject.add(invokeStmt);
		}

		SootClass hashMapClass = Scene.v().getSootClass("java.util.HashMap");
		NewExpr newHashMapExpr = Jimple.v().newNewExpr(RefType.v(hashMapClass));
		Local hashMapRefLocal = localGenerator.generateLocal(newHashMapExpr.getType());
		AssignStmt newHashMapAssignStmt = Jimple.v().newAssignStmt(hashMapRefLocal, newHashMapExpr);
		unitsToInject.add(newHashMapAssignStmt);

		SootMethod hashMapInit = hashMapClass.getMethod(SootMethod.constructorName, Collections.EMPTY_LIST);
		SpecialInvokeExpr hashMapInitExpr = Jimple.v().newSpecialInvokeExpr(hashMapRefLocal, hashMapInit.makeRef());
		unitsToInject.add(Jimple.v().newInvokeStmt(hashMapInitExpr));

		List<Type> hashMapAddArguments = new ArrayList<>();
		hashMapAddArguments.add(OBJECT_TYPE);
		hashMapAddArguments.add(OBJECT_TYPE);
		SootMethod hashMapPut = hashMapClass.getMethod("put", hashMapAddArguments);

		data.forEach((s, v) -> {
			List<Value> hashMapArgs = new ArrayList<>();
			if (s != null) {
				hashMapArgs.add(StringConstant.v(s));
				if (v == null) {
					hashMapArgs.add(NullConstant.v());
				} else {
					hashMapArgs.add(StringConstant.v(v));
				}
			}
			VirtualInvokeExpr virtualInvokeExpr = Jimple.v().newVirtualInvokeExpr(hashMapRefLocal, hashMapPut.makeRef(), hashMapArgs);
			InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(virtualInvokeExpr);
			unitsToInject.add(invokeStmt);
		});
		if (data.get("type").equals("reflection_call")) {
			List<Value> hashMapReflectionArgs = new ArrayList<>();
			hashMapReflectionArgs.add(StringConstant.v("virtualinvoke_base_ref"));
			hashMapReflectionArgs.add(((VirtualInvokeExpr) invokeExpr).getBase());
			VirtualInvokeExpr virtualInvokeExpr = Jimple.v().newVirtualInvokeExpr(hashMapRefLocal, hashMapPut.makeRef(), hashMapReflectionArgs);
			InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(virtualInvokeExpr);
			unitsToInject.add(invokeStmt);
		}


		groomLogArguments.add(hashMapRefLocal);
		groomLogArguments.add(arrayRefLocal);

		Value thisValue;
		if (!sootMethod.isStatic()) {
			thisValue = body.getThisLocal();
			if (invokeExpr.getMethod().isConstructor() && invokeExpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
				Value base = instanceInvokeExpr.getBase();
				if (instanceInvokeExpr.getMethod().isConstructor() && base.equals(thisValue)) {
					thisValue = NullConstant.v();
				}
			}
		} else {
			thisValue = NullConstant.v();
		}
		groomLogArguments.add(thisValue);

		SootMethod groomLog = GROOM.getMethodByName(GROOM_LOG_METHOD);
		InvokeExpr groomLogExpr = Jimple.v().newStaticInvokeExpr(groomLog.makeRef(), groomLogArguments);
		InvokeStmt groomLogStmt = Jimple.v().newInvokeStmt(groomLogExpr);
		unitsToInject.add(groomLogStmt);
		injectStatements(sootMethod, unitsToInject, unit);
		sootMethod.getActiveBody().validate();

		this.sootInstrumenter.injectedUnits.addAll(unitsToInject);
		System.out.println("Statement hooked: " + unit.toString());
		this.sootInstrumenter.injectedUnitsCount += unitsToInject.size();
		this.sootInstrumenter.statementHookedCount += 1;

	}

//	void hookMethod(SootMethod sootMethod) {
//		Body body = sootMethod.retrieveActiveBody();
//		CompleteUnitGraph unitGraph = new CompleteUnitGraph(body);
//		SimpleLocalDefs defs = new SimpleLocalDefs(unitGraph);
//		SimpleLocalUses uses = new SimpleLocalUses(unitGraph, defs);
//		List<Value> args = new ArrayList<>();
//		int intentParameterIndex = -1;
//		for (int i = 0; i < sootMethod.getParameterCount(); i++) {
//			Type parameterType = sootMethod.getParameterType(i);
//			if (parameterType.equals(INTENT_TYPE)) {
//				intentParameterIndex = i;
//			}
//		}
//		String logMethod = intentParameterIndex == -1 ? METHOD_LOG_GROOM_METHOD : INTENT_LOG_GROOM_METHOD;
//		final boolean[] onCreate = {false};
//		if (intentParameterIndex == -1) {
//			logMethod = METHOD_LOG_GROOM_METHOD;
//			IComponent component = app.getComponent(sootMethod.getDeclaringClass());
//			if (component != null) {
//				component.apply(new AbstractComponentSwitch() {
//					@Override
//					public void caseActivity(Activity activity) {
//						if (sootMethod.isStatic() || sootMethod.isStatic() || sootMethod.isConstructor()) {
//							return;
//						}
//						int isReceivedIntent = 0;
//						if (component.getCallback(sootMethod) != null && component.getCallback(sootMethod).getSootMethod().getName().equals("onResume")) {
//							isReceivedIntent = 1;
//						}
//
//						Local thisLocal = body.getThisLocal();
//						LocalGenerator localGenerator = new LocalGenerator(body);
//						Local intentLocal = localGenerator.generateLocal(INTENT_TYPE);
//						SootClass activityClass = Scene.v().getSootClass(Activity.SUPER_CLASS_NAME);
//						SootMethod getIntent = activityClass.getMethod("getIntent", Collections.emptyList(), INTENT_TYPE);
//						InvokeExpr invokeExpr1 = Jimple.v().newVirtualInvokeExpr(thisLocal, getIntent.makeRef());
//						AssignStmt assignStmt = Jimple.v().newAssignStmt(intentLocal, invokeExpr1);
//						List<Value> args1 = Arrays.asList(intentLocal, IntConstant.v(isReceivedIntent), StringConstant.v(sootMethod.getSignature()), thisLocal);
//						SootMethod groomLogIntent = GROOM.getMethodByName(INTENT_LOG_GROOM_METHOD);
//						InvokeExpr injectedExpr = Jimple.v().newStaticInvokeExpr(groomLogIntent.makeRef(), args1);
//						InvokeStmt injectedStmt = Jimple.v().newInvokeStmt(injectedExpr);
//
//						SpecialInvokeExpr specialInvokeExpr = Jimple.v().newSpecialInvokeExpr(thisLocal, Scene.v().getSootClass(sootMethod.getDeclaringClass().getSuperclass().getName()).getMethod(SootMethod.constructorName, Collections.emptyList()).makeRef());
//						if (!sootMethod.getParameterTypes().isEmpty()) {
//							int lastParamIndex = sootMethod.getParameterTypes().size() - 1;
//							Local lastParamLocal = body.getParameterLocal(lastParamIndex);
//							List<Unit> lastParamDefs = defs.getDefsOf(lastParamLocal);
//							Unit lastParamUnit = lastParamDefs.get(0);
////							if(body.getUnits().contains(sp))
//							body.getUnits().insertAfter(assignStmt, lastParamUnit);
//							body.getUnits().insertAfter(injectedStmt, assignStmt);
//						} else {
//							List<Unit> thisLocalDefs = defs.getDefsOf(body.getThisLocal());
//							Unit lastThisUnit = thisLocalDefs.get(0);
//							body.getUnits().insertAfter(assignStmt, lastThisUnit);
//							body.getUnits().insertAfter(injectedStmt, assignStmt);
//							body.validate();
//						}
//						onCreate[0] = true;
//					}
//
//				});
//			}
//			if (onCreate[0]) {
//				return;
//			}
//		} else {
//			Local intentLocal = body.getParameterLocal(intentParameterIndex);
//			args.add(intentLocal);
//			args.add(IntConstant.v(1));
//		}
//		args.add(StringConstant.v(sootMethod.getSignature()));
//		if (!sootMethod.isStatic()) {
//			Local thisLocal = body.getThisLocal();
//			args.add(thisLocal);
//		} else {
//			args.add(NullConstant.v());
//		}
//		SootMethod printString = GROOM.getMethodByName(logMethod);
//		InvokeExpr invokeExpr = Jimple.v().newStaticInvokeExpr(printString.makeRef(), args);
//		InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(invokeExpr);
//		if (sootMethod.isStaticInitializer() || sootMethod.isStatic()) {
//			body.getUnits().addFirst(invokeStmt);
//		} else if (!sootMethod.getParameterTypes().isEmpty()) {
//			int lastParamIndex = sootMethod.getParameterTypes().size() - 1;
//			Local lastParamLocal = body.getParameterLocal(lastParamIndex);
//			List<Unit> lastParamDefs = defs.getDefsOf(lastParamLocal);
//			Unit lastParamUnit = lastParamDefs.get(0);
//			body.getUnits().insertAfter(invokeStmt, lastParamUnit);
//		} else {
//			List<Unit> thisLocalDefs = defs.getDefsOf(body.getThisLocal());
//			Unit lastThisUnit = thisLocalDefs.get(0);
//			List<UnitValueBoxPair> use =
//					uses.getUsesOf(lastThisUnit);
//			for (UnitValueBoxPair u : use) {
//				List<UnitBox> unitBoxes = u.getUnit().getBoxesPointingToThis();
//				System.out.println("ici");
//			}
//			body.getUnits().insertAfter(invokeStmt, lastThisUnit);
//		}
//		body.validate();
//		System.out.println("method hooked" + sootMethod.getSignature());
//	}

	boolean isMethodCompliant(SootMethod sootMethod) {
		for (IComponent component : app.getComponents()) {
			if (component.isCallback(sootMethod)) {
				return true;
			}

		}
		for (IComponent component : app.getComponents()) {
			SootMethod componentMethod = component.getSootClass().getMethodUnsafe(sootMethod.getName(), sootMethod.getParameterTypes(), sootMethod.getReturnType());
			if (componentMethod != null) {
				return sootMethod.isConstructor() || sootMethod.isStaticInitializer();
			}
		}
		return false;
	}
}
