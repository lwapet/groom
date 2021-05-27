package fr.groom.apk_instrumentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;

public class InstrumenterUtils {
	public static String groomClassName = "fr.groom.Groom";

	public static File alignApk(File apkToAlign, String pathToZipAlign) throws IOException {
		System.out.println("Aligning apk. = " + apkToAlign.getAbsolutePath());
		File aligned = new File(apkToAlign.getAbsolutePath().replace(".apk", "") + "-aligned.apk");
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(pathToZipAlign + " " +
				"-v -p 4 " +
				apkToAlign + " " +
				aligned
		);
		InputStream error = pr.getErrorStream();
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
	public static void setEnv(String key, String value) {
		try {
			Map<String, String> env = System.getenv();
			Class<?> cl = env.getClass();
			Field field = cl.getDeclaredField("m");
			field.setAccessible(true);
			Map<String, String> writableEnv = (Map<String, String>) field.get(env);
			writableEnv.put(key, value);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to set environment variable", e);
		}
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
	public static void print_problematic_method(){
		Iterator<SootClass> classes = Scene.v().getApplicationClasses().snapshotIterator();
		int instruction_count=0;
		while (classes.hasNext()) {
			final SootClass c = classes.next();
			if(c.getName().contains("com.google.android.gms.internal.firebase-perf.yd")){
				for (SootMethod m : c.getMethods()) {
					Body body = m.retrieveActiveBody(c.getName());
					if(m.toString().contains("com.google.android.gms.internal.firebase-perf") && m.toString().contains("a(int,java.lang.Object[])")){
						System.out.println(" #####  >>>>  we print the method body in InstrumenterUtils; \n method name : " + m + " in thread : "
								+ Thread.currentThread().getId() + "class name: " +  c.getName() );
						System.out.println(" #####  >>>>  start ");
						for (Unit u : body.getUnits()) {
							System.out.println(instruction_count + ": " + u ); instruction_count++;
							if (u instanceof DefinitionStmt && (u.toString().contains("r15") || u.toString().contains("$r8"))) {
								DefinitionStmt astmt = (DefinitionStmt) u;
								Type leftType = Type.toMachineType(astmt.getLeftOp().getType());
								Type rightType = Type.toMachineType(astmt.getRightOp().getType());
								System.out.println("---> leftType = " + leftType);
								System.out.println("---> ryghtType = " + rightType);
							}
						}
						System.out.println(" #####  >>>>  end ");
					}	
				}
			}			
		}
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

