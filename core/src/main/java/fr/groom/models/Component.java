package fr.groom.models;

import org.json.JSONArray;
import org.json.JSONObject;
import soot.*;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public abstract class Component implements IComponent, Switchable {
	private SootClass sootClass;
	private SootClass superClass;
	private String id;
	private HashSet<Callback> callbacks;
	private List<IntentFilter> intentFilters;


	public Component(SootClass sootClass, SootClass superClass, String id, List<IntentFilter> intentFilters) {
		this.sootClass = sootClass;
		this.superClass = superClass;
		this.callbacks = callbacks;
		this.id = id;
		this.callbacks = new HashSet<>();
		this.intentFilters = intentFilters;
	}

	public SootClass getSootClass() {
		return sootClass;
	}

	@Override
	public String getId() {
		return this.id;
	}

	private void addCallbackMethod(Callback originalCallback) {
		SootClass componentClass = this.getSootClass();
		if (componentClass.isPhantom()) {
			return;
		}
		List<SootClass> encounteredSuperClasses = new ArrayList<>();
		SootClass superClass = this.getSootClass().getSuperclass();
		encounteredSuperClasses.add(superClass);
		if (superClass.getName().equals("java.lang.Object")) {
			return;
		}
		while (superClass.getSuperclass() != null && !superClass.getSuperclass().getName().equals("java.lang.Object")) {
			SootMethod superMethod = superClass.getMethodUnsafe(
					originalCallback.getSootMethod().getName(),
					originalCallback.getSootMethod().getParameterTypes(),
					originalCallback.getSootMethod().getReturnType()
			);
			if (superMethod != null && superMethod.isFinal()) {
				return;
			}
			superClass = superClass.getSuperclass();
			encounteredSuperClasses.add(superClass);
		}
		if(!encounteredSuperClasses.contains(this.superClass)) {
			return;
		}

		SootMethod originalCallbackMethod = originalCallback.getSootMethod();

		SootMethod existingCallback = componentClass.getMethodUnsafe(
				originalCallbackMethod.getName(),
				originalCallbackMethod.getParameterTypes(),
				originalCallbackMethod.getReturnType()
		);

		if (existingCallback != null) {
			if (originalCallbackMethod.isFinal()) {
				return;
			}
			callbacks.add(new Callback(existingCallback, originalCallback.isStart()));
			return;
		}
		SootMethod componentCallback = new SootMethod(
				originalCallbackMethod.getName(),
				originalCallbackMethod.getParameterTypes(),
				originalCallbackMethod.getReturnType(), Modifier.PROTECTED);
		componentClass.addMethod(componentCallback);
		JimpleBody body = Jimple.v().newBody(componentCallback);
		componentCallback.setActiveBody(body);
		LocalGenerator lg = new LocalGenerator(body);
		Local thisLocal = lg.generateLocal(componentClass.getType());
		ThisRef thisRef = Jimple.v().newThisRef(componentClass.getType());
		IdentityStmt thisRefStmt = Jimple.v().newIdentityStmt(thisLocal, thisRef);
		body.getUnits().addFirst(thisRefStmt);

		ArrayList<Local> paramLocals = new ArrayList<>();
		IntStream.range(0, componentCallback.getParameterTypes().size())
				.forEach(idx -> {
							Local paramLocal = lg.generateLocal(componentCallback.getParameterType(idx));
							paramLocals.add(paramLocal);
							Unit is = Jimple.v().newIdentityStmt(
									paramLocal,
									Jimple.v().newParameterRef(componentCallback.getParameterType(idx), idx)
							);
							body.getUnits().addLast(is);
						}
				);

		SpecialInvokeExpr superInvoke = Jimple.v().newSpecialInvokeExpr(thisLocal, originalCallback.getSootMethod().makeRef(), paramLocals);
		InvokeStmt superInvokeStmt = Jimple.v().newInvokeStmt(superInvoke);
		body.getUnits().addLast(superInvokeStmt);

		body.getUnits().addLast(Jimple.v().newReturnVoidStmt());
		body.validate();
		callbacks.add(new Callback(componentCallback, originalCallback.isStart()));
	}

	public void addCallbacks() {
		getOriginalCallbacks().forEach(this::addCallbackMethod);
	}

	@Override
	public Callback getCallback(SootMethod sootMethod) {
		return this.callbacks.stream()
				.filter(callback -> callback.getSootMethod().equals(sootMethod))
				.findFirst()
				.orElse(null);
	}

	@Override
	public HashSet<Callback> getCallbacks() {
		return callbacks;
	}

	@Override
	public boolean isCallback(SootMethod sootMethod) {
		return callbacks.stream().anyMatch(c -> c.getSootMethod().equals(sootMethod));
	}

	public List<IntentFilter> getIntentFilters() {
		return intentFilters;
	}

	public SootClass getSuperClass() {
		return superClass;
	}


	// create by build json plugin
	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		jo.put("class", sootClass.getName());
		jo.put("super_class", superClass.getName());
		JSONArray jsonArray = new JSONArray();
		intentFilters.forEach(i -> jsonArray.put(i.toJson()));
		jo.put("intent_filters", jsonArray);
		return jo;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
