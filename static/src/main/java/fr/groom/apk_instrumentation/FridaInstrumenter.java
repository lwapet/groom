package fr.groom.apk_instrumentation;

import fr.groom.Configuration;
import fr.groom.static_models.Application;
import fr.groom.static_models.Receiver;
import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FridaInstrumenter {
	private File instrumentedApk;
	private InvokeStmt fridaInjectionStmt;
	private InvokeStmt fridaDebugLogStmt;
	private ProcessManifest manifest;

	public FridaInstrumenter() {
		this.fridaInjectionStmt = buildFridaInjectionStatement();
		this.fridaDebugLogStmt = buildLogStatement();
	}

	public static File addSoFiles(File apk) {
		try {
			return addFridaSosFilesToZip(
					apk,
					Configuration.v().getFridaInstrumenterConfiguration().getFridaLibZipFile()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void injectFridaStatements(Application app) {
		System.out.println("Adding Frida server hooks");
		ArrayList<SootClass> componentsToHook = new ArrayList<>();
		if (Configuration.v().getFridaInstrumenterConfiguration().isHookActivities()) {
			componentsToHook.addAll(app.getLaunchableActivitySootClasses());
		}
		if (Configuration.v().getFridaInstrumenterConfiguration().isHookReceivers()) {
			for(Receiver receiver : app.getReveivers()) {
				componentsToHook.add(receiver.getSootClass());
			}
		}

		ArrayList<InvokeStmt> hookStatements = new ArrayList<>();
		hookStatements.add(this.fridaInjectionStmt);
		hookStatements.add(this.fridaDebugLogStmt);

		componentsToHook.forEach(componentToHook -> {
			System.out.println("Adding Frida hook to component: " + componentToHook.getName());
			InstrumenterUtils.instrumentClass(componentToHook, hookStatements);
		});
	}

	private static InvokeStmt buildFridaInjectionStatement() {
		List<Type> types = new ArrayList<>();
		types.add(RefType.v("java.lang.String"));
		SootMethod sootMethod = Scene.v().getSootClass("java.lang.System").getMethod("loadLibrary", types);
		Value value = StringConstant.v(Configuration.v().getFridaInstrumenterConfiguration().getFridaSoFilesName());
		SootMethodRef sootMethodRef = sootMethod.makeRef();
		StaticInvokeExpr staticInvokeExpr = Jimple.v().newStaticInvokeExpr(sootMethodRef, value);
		return Jimple.v().newInvokeStmt(staticInvokeExpr);
	}

	public static InvokeStmt buildLogStatement() {
		List<Type> types = new ArrayList<>();
		types.add(RefType.v("java.lang.String"));
		types.add(RefType.v("java.lang.String"));

		SootMethod logMethod = Scene.v().getSootClass("android.util.Log").getMethod("w", types);
		SootMethodRef logMethodRef = logMethod.makeRef();
		ArrayList<Value> arguments = new ArrayList<>();
		Value firstString = StringConstant.v("Frida-Gadget Loading");
		arguments.add(firstString);
		Value secondString = StringConstant.v("!!!!!!!!!!!!!!!!");
		arguments.add(secondString);
		StaticInvokeExpr staticInvokeExpr1 = Jimple.v().newStaticInvokeExpr(logMethodRef, arguments);
		return Jimple.v().newInvokeStmt(staticInvokeExpr1);
	}


	public static File addFridaSosFilesToZip(File apk, String pathToFridaLibZip) throws IOException {
		System.out.println("Adding frida .so files to apk.");
		File targetApk = new File(apk.getAbsolutePath().replace(".apk", "") + "-frida.apk");
		if (targetApk.exists()) {
			targetApk.delete();
		}
		ZipFile zipFile = new ZipFile(apk);

		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetApk));
		for (Enumeration e = zipFile.entries(); e.hasMoreElements(); ) {
			ZipEntry tempZipEntry = (ZipEntry) e.nextElement();
			ZipEntry entryIn = new ZipEntry(tempZipEntry.getName());
			zos.putNextEntry(entryIn);
			InputStream is1 = zipFile.getInputStream(entryIn);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = is1.read(buf1)) > 0) {
				zos.write(buf1, 0, len);
			}
		}

		ZipFile fridaLibZip = new ZipFile(pathToFridaLibZip);
		Enumeration<? extends ZipEntry> entries = fridaLibZip.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();

			InputStream stream = fridaLibZip.getInputStream(entry);
			ZipEntry test = zipFile.getEntry("lib/" + entry.getName());
			if (test == null) {
				zos.putNextEntry(new ZipEntry("lib/" + entry.getName()));

				byte[] buf2 = new byte[1024];
				int len2;
				while ((len2 = stream.read(buf2)) > 0) {
					zos.write(buf2, 0, len2);
				}
			}
		}
		zos.closeEntry();
		zos.close();
		return targetApk;
	}

}
