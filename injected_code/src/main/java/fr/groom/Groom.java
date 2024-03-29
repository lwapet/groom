package fr.groom;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.zip.GZIPOutputStream;


public class Groom {

	private static String MAIN_PATTERN;
	private static String COMPONENT_METHOD;
	private static String INTENT;
	private static String PACKAGE_NAME;
	private static String SHA_256;
	private static String DATA_MARKER;
	private static String SIGNATURE_KEY = "signature";
	private static String TYPE_KEY = "type";
	private static String CATEGORY_KEY = "category";
	private static String REFLECTION_TYPE = "reflection_call";
	private static Application app;
	private static PrintWriter out;
	private static Future<PrintWriter> futurPrinter;
	private static ExecutorService executor;
	private static UUID uuid = UUID.randomUUID();
	private static long startTime = System.currentTimeMillis();

	static {
		executor = Executors.newFixedThreadPool(1);
		futurPrinter = executor.submit(new Callable<PrintWriter>() {
			@Override
			public PrintWriter call() throws Exception {
				Socket clientSocket = new Socket("louisongitzinger.com", 1993);
				return new PrintWriter(clientSocket.getOutputStream(), true);
			}
		});
//
		try {
			final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
			final Method method = activityThreadClass.getMethod("currentApplication");
			app = (Application) method.invoke(null, (Object[]) null);
		} catch (final ClassNotFoundException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static String toCompressedBase64(byte[] bytes) {
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	public static byte[] compressToGzip(String str) {
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(obj);
			gzip.write(str.getBytes(StandardCharsets.UTF_8));
			gzip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj.toByteArray();
	}

	public static void sendData(JSONObject data) {
		try {
			data.put("monitoring_id", uuid.toString());
			data.put("sha256", SHA_256);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		executor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if (out == null) {
					try {
						out = futurPrinter.get();
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("type", "dynamic_analysis_start");
						jsonObject.put("timestamp", System.currentTimeMillis());
						sendData(jsonObject);
						return null;
					} catch (ExecutionException | InterruptedException e) {
						e.printStackTrace();
					}
				}
				out.println(MAIN_PATTERN + PACKAGE_NAME + "#" + prepareStringForDispatch(data.toString()));
				Log.d("GROOM", MAIN_PATTERN + PACKAGE_NAME + "#" + prepareStringForDispatch(data.toString()));
				return null;
			}
		});
	}


	public static JSONObject parseLayoutParams(WindowManager.LayoutParams layoutParams) {
		JSONObject data = new JSONObject();
//		NumberFormat nf = NumberFormat.getInstance();
//		nf.parse()
		try {
			data.put("alpha", layoutParams.alpha);
			data.put("buttonBrightness", layoutParams.buttonBrightness);
			data.put("dimAmount", layoutParams.dimAmount);
			data.put("flags", layoutParams.flags);
			data.put("format", layoutParams.format);
			data.put("format", layoutParams.gravity);
			data.put("horizontalMargin", layoutParams.horizontalMargin);
			data.put("horizontalWeight", layoutParams.horizontalWeight);
			data.put("memoryType", layoutParams.memoryType);
			data.put("packageName", layoutParams.packageName);
			data.put("preferredDisplayModeId", layoutParams.preferredDisplayModeId);
			data.put("preferredRefreshRate", layoutParams.preferredRefreshRate);
			data.put("rotationAnimation", layoutParams.rotationAnimation);
			data.put("screenBrightness", layoutParams.screenBrightness);
			data.put("screenOrientation", layoutParams.screenOrientation);
			data.put("softInputMode", layoutParams.softInputMode);
			data.put("systemUiVisibility", layoutParams.systemUiVisibility);
			data.put("x", layoutParams.x);
			data.put("y", layoutParams.y);
			data.put("token", layoutParams.token);
			data.put("verticalMargin", layoutParams.verticalMargin);
			data.put("verticalWeight", layoutParams.verticalWeight);
			data.put("windowAnimations", layoutParams.windowAnimations);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static JSONObject parseIntentFilter(IntentFilter intentFilter) {
		JSONObject intentfilterData = new JSONObject();
		try {

			JSONArray actions = new JSONArray();
			for (int i = 0; i < intentFilter.countActions(); i++) {
				actions.put(intentFilter.getAction(i));
			}
			intentfilterData.put("actions", actions);
			JSONArray categories = new JSONArray();
			for (int i = 0; i < intentFilter.countCategories(); i++) {
				categories.put(intentFilter.getCategory(i));
			}
			intentfilterData.put("categories", categories);
			JSONArray dataAuthorities = new JSONArray();
			for (int i = 0; i < intentFilter.countDataAuthorities(); i++) {
				IntentFilter.AuthorityEntry authorityEntry = intentFilter.getDataAuthority(i);
				JSONObject authorityEntryData = new JSONObject();
				authorityEntryData.put("host", authorityEntry.getHost());
				authorityEntryData.put("port", authorityEntry.getPort());
				dataAuthorities.put(authorityEntryData);
			}
			intentfilterData.put("authority_entries", dataAuthorities);
			JSONArray dataPaths = new JSONArray();
			for (int i = 0; i < intentFilter.countDataPaths(); i++) {
				PatternMatcher patternMatcher = intentFilter.getDataPath(i);
				JSONObject dataPathData = new JSONObject();
				dataPathData.put("path", patternMatcher.getPath());
				dataPathData.put("type", patternMatcher.getType());
				dataPaths.put(dataPathData);
			}
			intentfilterData.put("data_paths", dataPaths);
			JSONArray dataSchemes = new JSONArray();
			for (int i = 0; i < intentFilter.countDataSchemes(); i++) {
				dataSchemes.put(intentFilter.getDataScheme(i));
			}
			intentfilterData.put("data_schemes", dataSchemes);
			JSONArray dataSchemeSpecificParts = new JSONArray();
			for (int i = 0; i < intentFilter.countDataSchemeSpecificParts(); i++) {
				PatternMatcher patternMatcher = intentFilter.getDataPath(i);
				JSONObject dataSchemeData = new JSONObject();
				dataSchemeData.put("path", patternMatcher.getPath());
				dataSchemeData.put("type", patternMatcher.getType());
				dataSchemeSpecificParts.put(dataSchemeData);
			}
			intentfilterData.put("data_scheme_specific_parts", dataSchemeSpecificParts);
			JSONArray dataTypes = new JSONArray();
			for (int i = 0; i < intentFilter.countDataTypes(); i++) {
				dataTypes.put(intentFilter.getDataType(i));
			}
			intentfilterData.put("data_types", dataTypes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return intentfilterData;
	}

	public static void log(HashMap incomingData, ArrayList<Object> args, Object object) {
		JSONObject data = new JSONObject();
		try {
			if (incomingData.containsKey(SIGNATURE_KEY))
				data.put("method_signature", incomingData.get(SIGNATURE_KEY));
			if (incomingData.containsKey(TYPE_KEY))
				data.put("type", incomingData.get(TYPE_KEY));
			if (incomingData.containsKey(CATEGORY_KEY))
				data.put("category", incomingData.get(CATEGORY_KEY));
			data.put("timestamp", System.currentTimeMillis());
			String logType = (String) data.get("type");
			if (logType.equals(REFLECTION_TYPE)) {
				Log.d("LOUISON", "==========");
				//
				Object invokingClass =(Object) args.get(0);
				Object[] reflectionArgs = (Object[]) args.get(1);
				Method reflectionMethod = (Method) incomingData.get("virtualinvoke_base_ref");
				if(reflectionArgs != null) {
					args = new ArrayList<>();
					for(Object refArg : reflectionArgs) {
						args.add(refArg);
					}
				} else {
					args = new ArrayList<Object>();
				}
				if (invokingClass != null) {
					data.put("class_name", invokingClass.getClass().getCanonicalName());
				} else {
					data.put("class_name", reflectionMethod.getDeclaringClass().getCanonicalName());
				}
				data.put("method_name", reflectionMethod.getName());
			}
			JSONArray argumentDataArray = new JSONArray();
			for (Object arg : args) {
				JSONObject argumentData = new JSONObject();
				Object type;
				Object value;
				if (arg == null) {
					type = JSONObject.NULL;
				} else {
					type = arg.getClass().getCanonicalName();
				}
				if (arg == null) {
					value = JSONObject.NULL;
				} else if (arg instanceof WindowManager.LayoutParams) {
					value = parseLayoutParams((WindowManager.LayoutParams) arg);
				} else if (arg instanceof Intent) {
					JSONObject parsedIntent = null;
					Intent intent = (Intent) arg;
					if (intent.getStringExtra("UUID") == null) {
						intent.putExtra("UUID", UUID.randomUUID().toString());
					}
					try {
						parsedIntent = parseIntent(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					value = parsedIntent;
				} else if (arg instanceof BroadcastReceiver) {
					BroadcastReceiver broadcastReceiver = (BroadcastReceiver) arg;
					JSONObject broadcastReceiverData = new JSONObject();
					broadcastReceiverData.put("receiver_class", broadcastReceiver.getClass().getCanonicalName());
					value = broadcastReceiverData;
				} else if (arg instanceof IntentFilter) {
					IntentFilter intentFilter = (IntentFilter) arg;
					value = parseIntentFilter(intentFilter);
//				} else if (arg instanceof View) {
//					View view = (View) arg;
//					WindowManager windowManager = (WindowManager) app.getApplicationContext().getSystemService("window");
//
//					DisplayMetrics displayMetrics = new DisplayMetrics();
//					windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
//					view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//					Log.d("TEST OBS", String.valueOf(view.getMeasuredWidth()));
//					Log.d("TEST OBS", String.valueOf(view.getMeasuredHeight()));
//
				} else if (arg instanceof byte[]) {
					byte[] bytes = (byte[]) arg;
					value = new String(bytes);
				} else {
					value = arg.toString();
				}
				argumentData.put("type", type);
				argumentData.put("value", value);
				argumentDataArray.put(argumentData);
			}
			data.put("arguments", argumentDataArray);
			data.put("stack_trace", getStackTrace());
//			if (object != null) {
//				data.put("hash_code", object.hashCode());
//			} else {
//				data.put("hash_code", 0);
//			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendData(data);
	}

//	public static void log(String methodSignature, String information, ArrayList<Object> args, Object object) {
//		JSONObject data = new JSONObject();
//		try {
//			data.put("type", "standard_log");
//			data.put("method_signature", methodSignature);
//			data.put("information", information);
//			data.put("timestamp", System.currentTimeMillis());
//			JSONArray argumentDataArray = new JSONArray();
//			for (Object arg : args) {
//				JSONObject argumentData = new JSONObject();
//				Object type;
//				Object value;
//				if (arg == null) {
//					type = JSONObject.NULL;
//				} else {
//					type = arg.getClass().getCanonicalName();
//				}
//				if (arg == null) {
//					value = JSONObject.NULL;
//				} else if (arg instanceof WindowManager.LayoutParams) {
//					value = parseLayoutParams((WindowManager.LayoutParams) arg);
//				} else if (arg instanceof Intent) {
//					JSONObject parsedIntent = null;
//					Intent intent = (Intent) arg;
//					if (intent.getStringExtra("UUID") == null) {
//						intent.putExtra("UUID", UUID.randomUUID().toString());
//					}
//					try {
//						parsedIntent = parseIntent(intent);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					value = parsedIntent;
//				} else if (arg instanceof BroadcastReceiver) {
//					BroadcastReceiver broadcastReceiver = (BroadcastReceiver) arg;
//					JSONObject broadcastReceiverData = new JSONObject();
//					broadcastReceiverData.put("receiver_class", broadcastReceiver.getClass().getCanonicalName());
//					value = broadcastReceiverData;
//				} else if (arg instanceof IntentFilter) {
//					IntentFilter intentFilter = (IntentFilter) arg;
//					value = parseIntentFilter(intentFilter);
////				} else if (arg instanceof View) {
////					View view = (View) arg;
////					WindowManager windowManager = (WindowManager) app.getApplicationContext().getSystemService("window");
////
////					DisplayMetrics displayMetrics = new DisplayMetrics();
////					windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
////					view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
////					Log.d("TEST OBS", String.valueOf(view.getMeasuredWidth()));
////					Log.d("TEST OBS", String.valueOf(view.getMeasuredHeight()));
////
//				} else if (arg instanceof byte[]) {
//					byte[] bytes = (byte[]) arg;
//					value = new String(bytes);
//				} else {
//					value = arg.toString();
//				}
//				argumentData.put("type", type);
//				argumentData.put("value", value);
//				argumentDataArray.put(argumentData);
//			}
//			data.put("arguments", argumentDataArray);
//			data.put("stack_trace", getStackTrace());
//			if (object != null) {
//				data.put("hash_code", object.hashCode());
//			} else {
//				data.put("hash_code", 0);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		sendData(data);
//	}


	public static void logReflectionInvoke(Method method, Object invokingClass, Object[] args) {
		JSONObject data = new JSONObject();
		try {
			data.put("type", "");
			data.put("method_name", method.getName());
			data.put("timestamp", System.currentTimeMillis());
			if (invokingClass != null) {
				data.put("class_name", invokingClass.getClass().getCanonicalName());
			} else {
				data.put("class_name", method.getDeclaringClass().getCanonicalName());
			}
			JSONArray parsedArgs = new JSONArray();
			if (args != null) {
				for (Object arg : args) {
					JSONObject argumentData = new JSONObject();
					if (arg != null) {
						argumentData.put("type", arg.getClass().getCanonicalName());
						if (arg instanceof Intent) {
							argumentData.put("value", parseIntent((Intent) arg));
						} else {
							argumentData.put("value", arg.toString());
						}
					} else {
						argumentData.put("type", JSONObject.NULL);
						argumentData.put("value", JSONObject.NULL);
					}

					parsedArgs.put(argumentData);
				}
			}
			data.put("arguments", parsedArgs);
			data.put("stack_trace", getStackTrace());
		} catch (JSONException e) {
			e.printStackTrace();
		}

//		Log.w(MAIN_PATTERN + PACKAGE_NAME + "#" + "REFLECTION", prepareStringForDispatch(data.toString()));
		sendData(data);
	}

	private static JSONObject parseIntent(Intent intent) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (intent.getAction() != null) {
			jsonObject.put("action", intent.getAction());
		}
		if (intent.getCategories() != null) {
			JSONArray categoryArray = new JSONArray();
			for (String category : intent.getCategories()) {
				categoryArray.put(category);
			}
			jsonObject.put("categories", categoryArray);
		}
		if (intent.getExtras() != null) {
			Bundle bundle = intent.getExtras();
			JSONArray extraArray = new JSONArray();
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				JSONObject extra = new JSONObject();
				String compliantKey = key.replace(".", "$");
				if (value != null) {
					extra.put(compliantKey, value.toString());
				} else {
					extra.put(compliantKey, null);
				}
				extraArray.put(extra);
			}
			jsonObject.put("extras", extraArray);
		}
		if (intent.getFlags() != 0) {
			jsonObject.put("flags", intent.getFlags());
		}
		if (intent.getComponent() != null) {
			jsonObject.put("component", intent.getComponent().getClassName());
		}
		if (intent.getPackage() != null) {
			jsonObject.put("package", intent.getPackage());
		}
		if (intent.getType() != null) {
			jsonObject.put("type", intent.getType());
		}
		if (intent.getData() != null) {
			jsonObject.put("data", intent.getData().toString());
		}
		if (intent.getScheme() != null) {
			jsonObject.put("scheme", intent.getScheme());
		}
		if (intent.getSelector() != null) {
			jsonObject.put("selector", parseIntent(intent.getSelector()));
		}
		return jsonObject;
	}

	public static String prepareStringForDispatch(String stringToSend) {
		byte[] compressedData = compressToGzip(stringToSend);
		String encodedString = toCompressedBase64(compressedData);
		encodedString = encodedString.replace("\n", "").replace("\t", "");
		return DATA_MARKER + encodedString + DATA_MARKER;
	}

	public static JSONArray getStackTrace() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		return serializeStackTrace(stackTraceElements);
	}

	public static JSONArray serializeStackTrace(StackTraceElement[] stackTraceElements) {
		JSONArray array = new JSONArray();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			JSONArray elementArray = new JSONArray();
			elementArray.put(stackTraceElement.getClassName());
			elementArray.put(stackTraceElement.getMethodName());
			elementArray.put(stackTraceElement.getLineNumber());
			array.put(elementArray);
		}
		return array;
	}

//	public static void log(Intent intent, String methodSignature, Object object) {
//		int objectHashCode = -1;
//		JSONObject parsedIntent = null;
//		if (intent != null) {
//			if (intent.getStringExtra("UUID") == null) {
//				intent.putExtra("UUID", UUID.randomUUID().toString());
//			}
//			try {
//				parsedIntent = parseIntent(intent);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("method_signature", methodSignature);
//			jsonObject.put("stack_trace", serializeStackTrace(stackTraceElements));
//			jsonObject.put("intent", parsedIntent);
//			if (object != null) {
//				jsonObject.put("hash_code", objectHashCode);
//			} else {
//				jsonObject.put("hash_code", 0);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
////		int uncompressedLength = jsonObject.toString().length();
////		int compressedLength = compressData(jsonObject.toString()).length();
////		Log.w(MAIN_PATTERN + PACKAGE_NAME + "#" + INTENT, "uncompressedLength: " + uncompressedLength + "\ncompressedLength: " + compressedLength);
//		Log.w(MAIN_PATTERN + PACKAGE_NAME, prepareStringForDispatch(jsonObject.toString()));
//	}


	public static void logIntent(Intent intent, int isReceived, String methodSignature, Object object) {
//	public static void logIntent(Intent intent, int isReceived, String methodSignature) {
		boolean isReceivedBoolean = isReceived == 1;
		int objectHashCode = -1;
		JSONObject parsedIntent = null;
		if (intent != null) {
			if (intent.getStringExtra("UUID") == null) {
				intent.putExtra("UUID", UUID.randomUUID().toString());
			}
			try {
				parsedIntent = parseIntent(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method_signature", methodSignature);
			jsonObject.put("stack_trace", serializeStackTrace(stackTraceElements));
			jsonObject.put("intent", parsedIntent);
			if (object != null) {
				jsonObject.put("hash_code", objectHashCode);
			} else {
				jsonObject.put("hash_code", 0);
			}
			jsonObject.put("is_received", isReceivedBoolean);
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		int uncompressedLength = jsonObject.toString().length();
//		int compressedLength = compressData(jsonObject.toString()).length();
//		Log.w(MAIN_PATTERN + PACKAGE_NAME + "#" + INTENT, "uncompressedLength: " + uncompressedLength + "\ncompressedLength: " + compressedLength);
		Log.w(MAIN_PATTERN + PACKAGE_NAME + "#" + INTENT, prepareStringForDispatch(jsonObject.toString()));
	}

	public static void logMethod(String methodSignature, Object object) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		JSONObject jsonObject = new JSONObject();
		int objectHashCode = -1;
		if (object != null) {
			objectHashCode = object.hashCode();
		}
		try {
			jsonObject.put("method_signature", methodSignature);
			jsonObject.put("stack_trace", serializeStackTrace(stackTraceElements));
			jsonObject.put("hash_code", objectHashCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.w(MAIN_PATTERN + PACKAGE_NAME + "#" + COMPONENT_METHOD, prepareStringForDispatch(jsonObject.toString()));
	}
}
