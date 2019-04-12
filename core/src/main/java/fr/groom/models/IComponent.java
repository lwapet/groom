package fr.groom.models;

import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;

import java.util.HashSet;

public interface IComponent extends Switchable {
	SootClass getSootClass();

	String getId();

//	void transition(Callback callback, int intentHashCode);

//	public void setStateMachine(ComponentStateMachine stateMachine);

//	public void resetStateMachine();

//	public ComponentStateMachine getStateMachine();

	void addCallbacks();

	HashSet<Callback> getCallbacks();

	Callback getCallback(SootMethod sootMethod);

	HashSet<Callback> getOriginalCallbacks();

	boolean isCallback(SootMethod sootMethod);

	public JSONObject toJson();
}
