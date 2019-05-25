package a.a;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Encryptor {

//	public static String decrypt(String key, String initVector, String encrypted) {
//		try {
//			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//
//			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
//
//			return new String(original);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		return null;
//	}
//

	//	public static String encrypt(String key, String initVector, String value) {
//		try {
//			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
//
//			byte[] encrypted = cipher.doFinal(value.getBytes());
//			System.out.println("encrypted string: "
//					+ Base64.getEncoder().encodeToString(encrypted));
//
//			return Base64.getEncoder().encodeToString(encrypted);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return null;
//	}
	public static String decrypt(String key, String initVector, String encrypted) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException, UnsupportedEncodingException {
		byte[] tmp = android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT);
		Class<?> ivSpec = Class.forName("javax.crypto.spec.IvParameterSpec");
		Class<?> stringClass = Class.forName("java.lang.String");
		Class<?> keyClass = Class.forName("java.security.Key");
		Class<?> algoSpecClass = Class.forName("java.security.spec.AlgorithmParameterSpec");
		Constructor ivConst = ivSpec.getConstructor(byte[].class);
		Object ivSpecInst = ivConst.newInstance(initVector.getBytes("UTF-8"));
		Class<?> secretKey = Class.forName("javax.crypto.spec.SecretKeySpec");
		Constructor secretKeyConst = secretKey.getConstructor(byte[].class, stringClass);
		Object secretKeyInstance = secretKeyConst.newInstance(key.getBytes("UTF-8"), "AES");
		Class<?> cipher = Class.forName("javax.crypto.Cipher");
		Method getI = cipher.getDeclaredMethod("getInstance", stringClass);
		Object instance = getI.invoke(null, "AES/CBC/PKCS5Padding");
		Method init = cipher.getDeclaredMethod("init", int.class, keyClass, algoSpecClass);
		init.invoke(instance, 2, secretKeyInstance, ivSpecInst);
		Method doFinal = cipher.getDeclaredMethod("doFinal", byte[].class);
		Object result = doFinal.invoke(instance, tmp);
		return new String((byte[]) result);
	}
}
