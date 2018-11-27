package fr.groom.apk_instrumentation;

import soot.*;
import soot.jimple.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class InstrumenterUtils {
	public static String groomClassName = "fr.groom.Groom";

	public static File alignApk(File apkToAlign, String pathToZipAlign) throws IOException {
		System.out.println("Aligning apk.");
		File aligned = new File(apkToAlign.getAbsolutePath().replace(".apk", "") + "-aligned.apk");
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(pathToZipAlign + " " +
				"-v -p 4 " +
				apkToAlign + " " +
				aligned
		);
		InputStreamReader isr = new InputStreamReader(pr.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		try {
			pr.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return aligned;
	}

	public static File signApk(File apkToSign, String pathToApksigner, String pathToKeyStore, String keyPassword) throws IOException {
		System.out.println("Signing apk.");
		File signed = new File(apkToSign.getAbsolutePath().replace(".apk", "") + "-signed.apk");
		ProcessBuilder pb = new ProcessBuilder(pathToApksigner,
				"sign",
				"--ks",
				pathToKeyStore,
				"--key-pass",
				"pass:" + keyPassword,
				"--ks-pass",
				"pass:" + keyPassword,
				"--out",
				signed.getAbsolutePath(),
				apkToSign.getAbsolutePath()
		);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return signed;
	}



	public static SootMethod getInitMethod(SootClass sootClass) {
		SootMethod methodToReturn = null;
		for (SootMethod sootMethod : sootClass.getMethods()) {
			if (sootMethod.isStaticInitializer()) {
				return sootMethod;
			}
			if (sootMethod.isConstructor()) {
				methodToReturn = sootMethod;
			}
		}
		return methodToReturn;
	}


	public static SootMethod createStaticInitializer() {
		SootMethod staticInitializer = new SootMethod(SootMethod.staticInitializerName, null, VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
		Body body = Jimple.v().newBody(staticInitializer);
		staticInitializer.setActiveBody(body);
		body.getUnits().add(Jimple.v().newReturnVoidStmt());
		return staticInitializer;
	}

	public static void instrumentClass(SootClass sootClass, ArrayList<InvokeStmt> hookStatements) {
		if (hookStatements.isEmpty()) {
			System.err.println("No hook provided");
			return;
		}
		SootMethod initMethod = getInitMethod(sootClass);

		if (!initMethod.isStaticInitializer()) {
			initMethod = createStaticInitializer();
			sootClass.addMethod(initMethod);
		}
		Body body = initMethod.retrieveActiveBody();
		if (body.getUnits().isEmpty()) {
			body.getUnits().addAll(hookStatements);
		} else {
			Unit firstUnit = body.getUnits().getFirst();
			for (InvokeStmt hookStatement : hookStatements) {
				body.getUnits().insertBefore(hookStatement, firstUnit);
				firstUnit = hookStatement;
			}
		}
		body.validate();

		System.out.println(sootClass.getName() + " : instrumentation successful.");
	}

	public static void setGroomConstants(String constanteName, String value) {
		SootClass sootClass = Scene.v().getSootClass(groomClassName);
		RefType stringType = RefType.v("java.lang.String");
		SootField sootField = new SootField(constanteName, stringType);
		sootField = sootClass.getOrAddField(sootField);
		SootMethod sootMethod = sootClass.getMethodByName(SootMethod.staticInitializerName);
		sootMethod.retrieveActiveBody();
		SootFieldRef sootFieldRef = sootField.makeRef();
		StaticFieldRef staticFieldRef = Jimple.v().newStaticFieldRef(sootFieldRef);
		AssignStmt assignStmt = Jimple.v().newAssignStmt(staticFieldRef, StringConstant.v(value));
		sootMethod.getActiveBody().getUnits().insertBefore(assignStmt, sootMethod.getActiveBody().getUnits().getFirst());
		sootMethod.getActiveBody().validate();
	}
}

