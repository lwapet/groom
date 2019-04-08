package fr.groom;

import soot.*;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.*;

public class StringEncrypter extends SceneTransformer {

	private static String INTENT_CLASS_NAME = "android.content.Intent";
	private static String STRING_CLASS_NAME = "java.lang.String";
	private static String OBJECT_CLASS_NAME = "java.lang.Object";
	private static String CLASS_CLASS_NAME = "java.lang.Class";
	private static String REFLECTION_EXCETPION_CLASS_NAME = "java.lang.ReflectiveOperationException";
	public static Type STRING_TYPE = RefType.v(STRING_CLASS_NAME);
	public static Type INTENT_TYPE = RefType.v(INTENT_CLASS_NAME);
	public static Type OBJECT_TYPE = RefType.v(OBJECT_CLASS_NAME);
	public static Type CLASS_TYPE = RefType.v(CLASS_CLASS_NAME);
	public static Type REFLECTION_EXCEPTION_TYPE = RefType.v(REFLECTION_EXCETPION_CLASS_NAME);


	private void onStart() {
		System.out.println("HELLO");
	}

	private void onFinish() {
		System.out.println("Finished iteration through all units");
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
	public static String encrypt(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());
			System.out.println("encrypted string: "
					+ Base64.getEncoder().encodeToString(encrypted));

			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	//	private static void transformStatement(SootClass sootClass, SootMethod sootMethod, Unit unit) {
//
//	}

	private void encryptStrings(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		if(sootClass.getName().equals("com.lock.app.Encryptor")) {
			return;
		}
		Body body = sootMethod.retrieveActiveBody();
		LocalGenerator generator = new LocalGenerator(body);

		Stmt stmt = (Stmt) unit;
		if(stmt.containsInvokeExpr()) {
			InvokeExpr invokeExpr = stmt.getInvokeExpr();
			int count = 0;
			for(Value arg : invokeExpr.getArgs()) {
				if(arg instanceof StringConstant) {
					StringConstant cst = (StringConstant) arg;
					System.out.println(cst.value);
					Local decryptedStringLocal = generator.generateLocal(STRING_TYPE);
					Local encryptedStringLocal = generator.generateLocal(STRING_TYPE);
					Local keyStringLocal = generator.generateLocal(STRING_TYPE);
					Local ivStringLocal = generator.generateLocal(STRING_TYPE);
					String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
					RandomString tickets = new RandomString(16, new SecureRandom(), easy);
					String key = tickets.nextString();
					String iv = tickets.nextString();
					AssignStmt keyStringAssignStmt = Jimple.v().newAssignStmt(keyStringLocal, StringConstant.v(key));
					body.getUnits().insertBefore(keyStringAssignStmt, unit);
					AssignStmt ivStringAssignStmt = Jimple.v().newAssignStmt(ivStringLocal, StringConstant.v(iv));
					body.getUnits().insertAfter(ivStringAssignStmt, keyStringAssignStmt);
					String encryptedString = encrypt(key, iv, cst.value);
					AssignStmt encryptedStringAssignStmt = Jimple.v().newAssignStmt(encryptedStringLocal, StringConstant.v(encryptedString));
					body.getUnits().insertAfter(encryptedStringAssignStmt, ivStringAssignStmt);
					SootClass encryptorClass = Scene.v().getSootClass("com.lock.app.Encryptor");
					List<Type> decryptMethodTypes = new ArrayList<>();
					decryptMethodTypes.add(STRING_TYPE);
					decryptMethodTypes.add(STRING_TYPE);
					decryptMethodTypes.add(STRING_TYPE);
					SootMethod decryptMethod = encryptorClass.getMethod("decrypt", decryptMethodTypes);
					List<Value> decryptArgs = new ArrayList<>();
					decryptArgs.add(keyStringLocal);
					decryptArgs.add(ivStringLocal);
					decryptArgs.add(encryptedStringLocal);
					StaticInvokeExpr decryptExpr = Jimple.v().newStaticInvokeExpr(decryptMethod.makeRef(), decryptArgs);
					AssignStmt decryptedStringStmt = Jimple.v().newAssignStmt(decryptedStringLocal, decryptExpr);
					body.getUnits().insertAfter(decryptedStringStmt, encryptedStringAssignStmt);
					invokeExpr.setArg(count, decryptedStringLocal);
					body.validate();
				}
				count++;
			}
		}
		if(stmt instanceof AssignStmt) {
			AssignStmt assignStmt = (AssignStmt) stmt;
			if(assignStmt.getRightOp() instanceof StringConstant) {
				StringConstant cst = (StringConstant) assignStmt.getRightOp();
				System.out.println(cst);
				Local decryptedStringLocal = generator.generateLocal(STRING_TYPE);
				Local encryptedStringLocal = generator.generateLocal(STRING_TYPE);
				Local keyStringLocal = generator.generateLocal(STRING_TYPE);
				Local ivStringLocal = generator.generateLocal(STRING_TYPE);
				String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
				RandomString tickets = new RandomString(16, new SecureRandom(), easy);
				String key = tickets.nextString();
				String iv = tickets.nextString();
				AssignStmt keyStringAssignStmt = Jimple.v().newAssignStmt(keyStringLocal, StringConstant.v(key));
				body.getUnits().insertBefore(keyStringAssignStmt, unit);
				AssignStmt ivStringAssignStmt = Jimple.v().newAssignStmt(ivStringLocal, StringConstant.v(iv));
				body.getUnits().insertAfter(ivStringAssignStmt, keyStringAssignStmt);
				String encryptedString = encrypt(key, iv, cst.value);
				AssignStmt encryptedStringAssignStmt = Jimple.v().newAssignStmt(encryptedStringLocal, StringConstant.v(encryptedString));
				body.getUnits().insertAfter(encryptedStringAssignStmt, ivStringAssignStmt);
				SootClass encryptorClass = Scene.v().getSootClass("com.lock.app.Encryptor");
				List<Type> decryptMethodTypes = new ArrayList<>();
				decryptMethodTypes.add(STRING_TYPE);
				decryptMethodTypes.add(STRING_TYPE);
				decryptMethodTypes.add(STRING_TYPE);
				SootMethod decryptMethod = encryptorClass.getMethod("decrypt", decryptMethodTypes);
				List<Value> decryptArgs = new ArrayList<>();
				decryptArgs.add(keyStringLocal);
				decryptArgs.add(ivStringLocal);
				decryptArgs.add(encryptedStringLocal);
				StaticInvokeExpr decryptExpr = Jimple.v().newStaticInvokeExpr(decryptMethod.makeRef(), decryptArgs);
				AssignStmt decryptedStringStmt = Jimple.v().newAssignStmt(decryptedStringLocal, decryptExpr);

				body.getUnits().insertAfter(decryptedStringStmt, encryptedStringAssignStmt);
				assignStmt.setRightOp(decryptedStringLocal);
				body.validate();
			}
		}
	}

	private void handleUnit(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		encryptStrings(sootClass, sootMethod, unit);
	}
//

	@Override
	protected void internalTransform(String phaseName, Map<String, String> options) {
		onStart();
		Iterator<SootClass> sootClassIterator = Scene.v().getApplicationClasses().snapshotIterator();
		System.out.println("Starting StringEncrypter iteration through every unit.");
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
