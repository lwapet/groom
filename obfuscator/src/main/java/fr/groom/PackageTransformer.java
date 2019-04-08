package fr.groom;

import soot.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PackageTransformer extends SceneTransformer {
	private void onStart() {
		System.out.println("HELLO");
	}

	private void onFinish() {
		System.out.println("Finished iteration through all units");
	}
	private void handleClass(SootClass sootClass) {

	}


	private void handleMethod(SootClass sootClass, SootMethod sootMethod) {

	}

	//	private static SootClass getMatchingSuperClass(SootClass klass) {
//		List<SootClass> encounteredSuperClasses = new ArrayList<>();
//		SootClass superClass = klass.getSuperclass();
//		encounteredSuperClasses.add(superClass);
//		while (superClass.getSuperclass() != null && !superClass.getSuperclass().getName().equals("java.lang.Object")) {
//			superClass = superClass.getSuperclass();
//			encounteredSuperClasses.add(superClass);
//		}
//	}
	private void handleUnit(SootClass sootClass, SootMethod sootMethod, Unit unit) {

	}
	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		onStart();
		Iterator<SootClass> sootClassIterator = Scene.v().getApplicationClasses().snapshotIterator();
		System.out.println("Starting ReflectionTransformer iteration through every unit.");
		while (sootClassIterator.hasNext()) {
			final SootClass sootClass = sootClassIterator.next();
			if (sootClass.getName().startsWith("android.support")) {
				continue;
			}

			List<SootMethod> clone = new ArrayList<>(sootClass.getMethods());
			for (SootMethod sootMethod : clone) {
				if (sootMethod.isConcrete()) {
					Body body;
					if (!sootMethod.hasActiveBody()) {
						body = sootMethod.retrieveActiveBody();
					} else {
						body = sootMethod.getActiveBody();
					}
					for (Iterator<Unit> uIterator = body.getUnits().snapshotIterator();
						 uIterator.hasNext(); ) {
						Unit unit = uIterator.next();
						handleUnit(sootClass, sootMethod, unit);
					}
					handleMethod(sootClass, sootMethod);
				}
			}
		}
		onFinish();
	}

}
