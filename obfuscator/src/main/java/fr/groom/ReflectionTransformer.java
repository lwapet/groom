package fr.groom;

import soot.*;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.*;
import soot.jimple.internal.JArrayRef;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ReflectionTransformer extends SceneTransformer {

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

	public static String[] START_SERVICE = {
			"startService(android.content.Intent,android.os.Bundle)>",
			"startService(android.content.Intent)>"
	};
	public static String[] START_ACTIVITY = {
			"startActivity(android.content.Intent)>",
			"startActivity(android.content.Intent,android.os.Bundle)>"
	};
	public static String[] TO_TRANSFORM = {
			"<android.view.WindowManager: void addView(android.view.View,android.view.ViewGroup$LayoutParams)>",
			"<android.view.ViewManager: void addView(android.view.View,android.view.ViewGroup$LayoutParams)>",
			"<android.view.LayoutInflater: android.view.View inflate(int,android.view.ViewGroup)>",
			"<com.android.internal.telephony.cdma.CDMAPhone: java.lang.String getDeviceId()>",
			"<com.android.internal.telephony.IPhoneSubInfo$Stub$Proxy: java.lang.String getDeviceId()>",
			"<com.android.internal.telephony.gsm.GSMPhone: java.lang.String getDeviceId()>",
			"<com.android.internal.telephony.sip.SipPhoneBase: java.lang.String getDeviceId()>",
			"<com.android.internal.telephony.PhoneSubInfo: java.lang.String getDeviceId()>",
			"<com.android.internal.telephony.PhoneProxy: java.lang.String getDeviceId()>",
			"<android.telephony.TelephonyManager: java.lang.String getDeviceId()>",
			"<com.android.internal.telephony.PhoneSubInfoProxy: java.lang.String getDeviceId()>",
			"<com.android.internal.telephony.sip.SipPhone: java.lang.String getDeviceId()>",
			"<com.android.email.service.AccountService$1: java.lang.String getDeviceId()>",
			"<com.android.emailcommon.service.IAccountService$Stub$Proxy: java.lang.String getDeviceId()>",
			"<com.android.emailcommon.service.AccountServiceProxy: java.lang.String getDeviceId()>",
			"<android.hardware.usb.UsbDevice: int getDeviceId(java.lang.String)>",
			"<android.view.InputDevice: int[] getDeviceIds()>",
			"<android.hardware.usb.UsbDevice: int getDeviceId()>",
			"<android.view.MotionEvent: int getDeviceId()>",
			"<android.mtp.MtpDevice: int getDeviceId()>",
			"<android.view.KeyEvent: int getDeviceId()>",
			"<com.android.exchange.ExchangeService: java.lang.String getDeviceId(android.content.Context)>"

	};
	private String[] reflectionInvokeMethods = {
			"<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>"
	};

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
			}
		}
	}

	private void handleUnit(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		toReflection(sootClass, sootMethod, unit);
		encryptStrings(sootClass, sootMethod, unit);
	}
//
	private void toReflection(SootClass sootClass, SootMethod sootMethod, Unit unit) {
//		if (unit.toString().contains("inflate")) {
//			System.out.println("ci");
//		}
		Stmt stmt = (Stmt) unit;
		if (!stmt.containsInvokeExpr()) {
			return;
		}
		InvokeExpr cInvokExpr = stmt.getInvokeExpr();
		if (Arrays.stream(START_SERVICE).anyMatch(s -> unit.toString().contains(s))
				|| Arrays.stream(START_ACTIVITY).anyMatch(s -> unit.toString().contains(s))
				|| Arrays.stream(TO_TRANSFORM).anyMatch(s -> cInvokExpr.getMethod().getSignature().equals(s))
		) {

			SootMethod toCall = cInvokExpr.getMethod();
			Body body = sootMethod.retrieveActiveBody();
			Value baseOrThis;
			if (cInvokExpr instanceof VirtualInvokeExpr) {
				VirtualInvokeExpr cVirtualInvokeExpr = (VirtualInvokeExpr) cInvokExpr;
				baseOrThis = cVirtualInvokeExpr.getBase();
			} else if (cInvokExpr instanceof InterfaceInvokeExpr) {
				InterfaceInvokeExpr interfaceInvokeExpr = (InterfaceInvokeExpr) cInvokExpr;
				baseOrThis = interfaceInvokeExpr.getBase();
			} else {
				baseOrThis = body.getThisLocal();
			}

//			SootClass toInvoke = null;
//			SootClass currentClass = sootClass;
//			while(toInvoke == null) {
//				for(SootMethod method : currentClass.getMethods()) {
//					if(method.equals(sootMethod)){
//				}
//				currentClass.getMethods().stream().forEach(m -> {
//					if(m.equals(sootMethod)) {
//						System.out.println("cic");
//					}else {
//						if(currentClass.getSuperclass() != null) {
//							currentClass = currentClass.getSuperclass();
//						}
//					}
//				});
//			}


			LocalGenerator localGenerator = new LocalGenerator(body);

			Local decryptedClassNameStringLocal = localGenerator.generateLocal(STRING_TYPE);
			Local decryptedMethodNameStringLocal = localGenerator.generateLocal(STRING_TYPE);
			AssignStmt decryptedClassNameStmt = null;
			AssignStmt decryptedMethodNameStmt = null;
			if (Configuration.v().getUseEncryption()) {
				Local classStringLocal = localGenerator.generateLocal(STRING_TYPE);
				Local keyStringLocal = localGenerator.generateLocal(STRING_TYPE);
				Local ivStringLocal = localGenerator.generateLocal(STRING_TYPE);
				String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXYabcdefhijkprstuvwx";
				RandomString tickets = new RandomString(16, new SecureRandom(), easy);
				String key = tickets.nextString();
				String iv = tickets.nextString();
				AssignStmt keyStringAssignStmt = Jimple.v().newAssignStmt(keyStringLocal, StringConstant.v(key));
				body.getUnits().insertAfter(keyStringAssignStmt, unit);
				AssignStmt ivStringAssignStmt = Jimple.v().newAssignStmt(ivStringLocal, StringConstant.v(iv));
				body.getUnits().insertAfter(ivStringAssignStmt, keyStringAssignStmt);
				String encryptedClassStringLocal = encrypt(key, iv, toCall.getDeclaringClass().getName());
				AssignStmt classStringAssignStmt = Jimple.v().newAssignStmt(classStringLocal, StringConstant.v(encryptedClassStringLocal));
				body.getUnits().insertAfter(classStringAssignStmt, ivStringAssignStmt);
				Local methodStringLocal = localGenerator.generateLocal(STRING_TYPE);
				String encryptedMethodStringLocal = encrypt(key, iv, toCall.getName());
				AssignStmt methodNameStringAssignStmt = Jimple.v().newAssignStmt(methodStringLocal, StringConstant.v(encryptedMethodStringLocal));
				body.getUnits().insertAfter(methodNameStringAssignStmt, classStringAssignStmt);

				SootClass encryptorClass = Scene.v().getSootClass("com.lock.app.Encryptor");
				List<Type> decryptMethodTypes = new ArrayList<>();
				decryptMethodTypes.add(STRING_TYPE);
				decryptMethodTypes.add(STRING_TYPE);
				decryptMethodTypes.add(STRING_TYPE);
				SootMethod decryptMethod = encryptorClass.getMethod("decrypt", decryptMethodTypes);
				List<Value> decryptArgs = new ArrayList<>();
				decryptArgs.add(keyStringLocal);
				decryptArgs.add(ivStringLocal);
				decryptArgs.add(classStringLocal);
				StaticInvokeExpr decryptExpr = Jimple.v().newStaticInvokeExpr(decryptMethod.makeRef(), decryptArgs);
				decryptedClassNameStmt = Jimple.v().newAssignStmt(decryptedClassNameStringLocal, decryptExpr);
				body.getUnits().insertAfter(decryptedClassNameStmt, methodNameStringAssignStmt);

				List<Value> decryptMethodNameArgs = new ArrayList<>();
				decryptMethodNameArgs.add(keyStringLocal);
				decryptMethodNameArgs.add(ivStringLocal);
				decryptMethodNameArgs.add(methodStringLocal);
				StaticInvokeExpr decryptMehthodNameExpr = Jimple.v().newStaticInvokeExpr(decryptMethod.makeRef(), decryptMethodNameArgs);
				decryptedMethodNameStmt = Jimple.v().newAssignStmt(decryptedMethodNameStringLocal, decryptMehthodNameExpr);
				body.getUnits().insertAfter(decryptedMethodNameStmt, decryptedClassNameStmt);

			} else {
				decryptedClassNameStmt = Jimple.v().newAssignStmt(decryptedClassNameStringLocal, StringConstant.v(toCall.getDeclaringClass().getName()));
				body.getUnits().insertAfter(decryptedClassNameStmt, unit);
				decryptedMethodNameStmt = Jimple.v().newAssignStmt(decryptedMethodNameStringLocal, StringConstant.v(toCall.getName()));
				body.getUnits().insertAfter(decryptedMethodNameStmt, decryptedClassNameStmt);
			}


			Local forNameClassLocal = localGenerator.generateLocal(CLASS_TYPE);

			SootClass klass = Scene.v().getSootClass(CLASS_CLASS_NAME);
			SootMethod forName = klass.getMethod("forName", Collections.singletonList(STRING_TYPE), RefType.v(klass));
			InvokeExpr invokeExpr = Jimple.v().newStaticInvokeExpr(forName.makeRef(), decryptedClassNameStringLocal);
			AssignStmt forNameAssignStmt = Jimple.v().newAssignStmt(forNameClassLocal, invokeExpr);
			body.getUnits().insertAfter(forNameAssignStmt, decryptedMethodNameStmt);


			Local getDeclaredMethodArrayRef = localGenerator.generateLocal(ArrayType.v(RefType.v(klass), 1));
			NewArrayExpr getDeclaredNewArrayExpr = Jimple.v().newNewArrayExpr(RefType.v(klass), IntConstant.v(cInvokExpr.getArgCount()));
			AssignStmt getDeclaredMethodAssignStmt = Jimple.v().newAssignStmt(getDeclaredMethodArrayRef, getDeclaredNewArrayExpr);
			body.getUnits().insertAfter(getDeclaredMethodAssignStmt, forNameAssignStmt);

//			ArrayRef arrayRef1 = Jimple.v().newArrayRef(arrayRef, IntConstant.v(0));
//			ClassConstant intentClass = ClassConstant.fromType(INTENT_TYPE);
//			AssignStmt assignStmt1 = Jimple.v().newAssignStmt(arrayRef1, intentClass);
//			body.getUnits().insertAfter(assignStmt1, newArrayAssignStmt);

			int getDeclaredCount = 0;
			Unit getDeclaredPointer = getDeclaredMethodAssignStmt;
			for (Value arg : cInvokExpr.getArgs()) {
				ArrayRef tempRef = Jimple.v().newArrayRef(getDeclaredMethodArrayRef, IntConstant.v(getDeclaredCount));
				AssignStmt tempsStmt = Jimple.v().newAssignStmt(tempRef, ClassConstant.fromType(cInvokExpr.getMethod().getParameterType(getDeclaredCount)));
				body.getUnits().insertAfter(tempsStmt, getDeclaredPointer);
				getDeclaredCount++;
				getDeclaredPointer = tempsStmt;
			}

			List<Type> reflectMethodTypes = new ArrayList<>();
			reflectMethodTypes.add(STRING_TYPE);
			reflectMethodTypes.add(ArrayType.v(RefType.v(klass), 1));


			SootClass reflectMethodClass = Scene.v().getSootClass("java.lang.reflect.Method");
			SootMethod reflectMethod = klass.getMethod("getDeclaredMethod", reflectMethodTypes, RefType.v(reflectMethodClass));
			Local declaredMethodInvokeLocal = localGenerator.generateLocal(RefType.v("java.lang.reflect.Method"));
			List<Value> arguments = new ArrayList<>();
			arguments.add(decryptedMethodNameStringLocal);
			arguments.add(getDeclaredMethodArrayRef);

			VirtualInvokeExpr declaredMethodExpr = Jimple.v().newVirtualInvokeExpr(forNameClassLocal, reflectMethod.makeRef(), arguments);
			AssignStmt getDeclaredMethodStmt = Jimple.v().newAssignStmt(declaredMethodInvokeLocal, declaredMethodExpr);
			body.getUnits().insertAfter(getDeclaredMethodStmt, getDeclaredPointer);


			Local invokeArrayRef = localGenerator.generateLocal(ArrayType.v(OBJECT_TYPE, 1));
			NewArrayExpr invokeArrayExpr = Jimple.v().newNewArrayExpr(OBJECT_TYPE, IntConstant.v(cInvokExpr.getArgCount()));
			AssignStmt invokeArrayAssignStmt = Jimple.v().newAssignStmt(invokeArrayRef, invokeArrayExpr);
			body.getUnits().insertAfter(invokeArrayAssignStmt, getDeclaredMethodStmt);

			int count = 0;
			Unit pointer = invokeArrayAssignStmt;
			for (Value arg : cInvokExpr.getArgs()) {
				ArrayRef tempRef = Jimple.v().newArrayRef(invokeArrayRef, IntConstant.v(count));
//				ClassConstant tempClassConstant = ClassConstant.fromType(arg.getType());
				AssignStmt tempsStmt;
				if (arg.getType() instanceof PrimType) {
					PrimType primType = (PrimType) arg.getType();
					Local boxedLocal = localGenerator.generateLocal(primType.boxedType());
					SootClass boxedClass = Scene.v().getSootClass(primType.boxedType().getClassName());
					SootMethod boxedInit = boxedClass.getMethod("valueOf", Collections.singletonList(primType));
					StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(boxedInit.makeRef(), Collections.singletonList(arg));
					AssignStmt assignStmt = Jimple.v().newAssignStmt(boxedLocal, staticInvokeExpr);
					body.getUnits().insertAfter(assignStmt, pointer);
					pointer = assignStmt;
					tempsStmt = Jimple.v().newAssignStmt(tempRef, boxedLocal);
				} else {
					tempsStmt = Jimple.v().newAssignStmt(tempRef, arg);
				}
				body.getUnits().insertAfter(tempsStmt, pointer);
				count++;
				pointer = tempsStmt;
			}

			List<Type> reflectInvokeMethodTypes = new ArrayList<>();
			reflectInvokeMethodTypes.add(OBJECT_TYPE);
			reflectInvokeMethodTypes.add(ArrayType.v(OBJECT_TYPE, 1));
			SootMethod reflectInvokeMethod = reflectMethodClass.getMethod("invoke", reflectInvokeMethodTypes, OBJECT_TYPE);
			List<Value> args = new ArrayList<>();
			args.add(baseOrThis);
			args.add(invokeArrayRef);
			VirtualInvokeExpr reflectInvokeMethodExpr = Jimple.v().newVirtualInvokeExpr(declaredMethodInvokeLocal, reflectInvokeMethod.makeRef(), args);
			Stmt resultStmt;
			Stmt castStmt = null;
			if(stmt instanceof AssignStmt) {
				AssignStmt resultAssignStmt = (AssignStmt) stmt;
				Local toBeCasted = localGenerator.generateLocal(OBJECT_TYPE);

				Value resultReference = resultAssignStmt.getLeftOp();
				resultStmt = Jimple.v().newAssignStmt(toBeCasted, reflectInvokeMethodExpr);
				CastExpr castExpr = Jimple.v().newCastExpr(toBeCasted, resultReference.getType());
				castStmt = Jimple.v().newAssignStmt(resultReference, castExpr);
			} else {
				resultStmt = Jimple.v().newInvokeStmt(reflectInvokeMethodExpr);
			}
			body.getUnits().insertAfter(resultStmt, pointer);
			if(castStmt != null) {
				body.getUnits().insertAfter(castStmt, resultStmt);
			}

			Local exceptionLocal = localGenerator.generateLocal(REFLECTION_EXCEPTION_TYPE);
			CaughtExceptionRef caughtExceptionRef = Jimple.v().newCaughtExceptionRef();
			IdentityStmt exceptionStmt = Jimple.v().newIdentityStmt(exceptionLocal, caughtExceptionRef);
//			body.getUnits().insertAfter(exceptionStmt, reflectInvokeMethodStmt);


			SootClass reflectiveOperationException = Scene.v().getSootClass(REFLECTION_EXCETPION_CLASS_NAME);

			SootMethodRefImpl sootMethodRef = new SootMethodRefImpl(reflectiveOperationException, "printStackTrace", Collections.emptyList(), VoidType.v(), false);
			VirtualInvokeExpr printStackTraceInvokeExpr = Jimple.v().newVirtualInvokeExpr(exceptionLocal, sootMethodRef);
			InvokeStmt printStackTraceInvokeStmt = Jimple.v().newInvokeStmt(printStackTraceInvokeExpr);
//			body.getUnits().insertAfter(printStackTraceInvokeStmt,exceptionStmt);


			List<Stmt> returnStmts = new ArrayList<>();
			for (Unit bodyUnit : body.getUnits()) {
				if (bodyUnit instanceof ReturnStmt) {
					returnStmts.add((ReturnStmt) bodyUnit);
				} else if (bodyUnit instanceof ReturnVoidStmt) {
					returnStmts.add((ReturnVoidStmt) bodyUnit);
				}
			}
			Stmt lastReturnStmt = returnStmts.get(returnStmts.size() - 1);
			body.getUnits().insertBefore(printStackTraceInvokeStmt, lastReturnStmt);
			body.getUnits().insertBefore(exceptionStmt, printStackTraceInvokeStmt);
			Value returnValue;
			if (lastReturnStmt instanceof ReturnStmt) {
				ReturnStmt returnStmt = (ReturnStmt) lastReturnStmt;
				returnValue = returnStmt.getOp();
				body.getUnits().insertBefore(Jimple.v().newReturnStmt(returnValue), exceptionStmt);
			} else if (lastReturnStmt instanceof ReturnVoidStmt) {
				body.getUnits().insertBefore(Jimple.v().newReturnVoidStmt(), exceptionStmt);
			}

			Trap forNameTrap = Jimple.v().newTrap(Scene.v().getSootClass(REFLECTION_EXCETPION_CLASS_NAME), forNameAssignStmt, body.getUnits().getSuccOf(forNameAssignStmt), exceptionStmt);
			body.getTraps().addFirst(forNameTrap);
			Trap getDeclaredTrap = Jimple.v().newTrap(Scene.v().getSootClass(REFLECTION_EXCETPION_CLASS_NAME), getDeclaredMethodStmt, body.getUnits().getSuccOf(getDeclaredMethodStmt), exceptionStmt);
			body.getTraps().insertAfter(getDeclaredTrap, forNameTrap);
			Trap invokeTrap = Jimple.v().newTrap(reflectiveOperationException, resultStmt, body.getUnits().getSuccOf(resultStmt), exceptionStmt);
			body.getTraps().insertAfter(invokeTrap, getDeclaredTrap);

			body.getUnits().remove(unit);
			body.validate();


			System.out.println("METHOD INSTRUMENTED: sootMethod: " + sootMethod.getName() + " unit: " + unit.toString());

		}
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