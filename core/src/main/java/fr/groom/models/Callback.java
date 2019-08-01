package fr.groom.models;

import soot.Scene;
import soot.SootMethod;

public class Callback {
	private String signature;
	private SootMethod sootMethod;
	private boolean isStart;

	public Callback(String signature, boolean isStart) {
		this.signature = signature;
		try {
			this.sootMethod = Scene.v().getMethod(signature);
		} catch(RuntimeException e) {
			this.sootMethod = null;
		}
		this.isStart = isStart;
	}

	public Callback(SootMethod sootMethod, boolean isStart) {
		this.sootMethod = sootMethod;
		this.isStart = isStart;
	}

	public SootMethod getSootMethod() {
		return sootMethod;
	}

	public boolean isStart() {
		return isStart;
	}
}
