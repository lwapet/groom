package fr.groom;

import soot.Scene;
import soot.options.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static soot.SootClass.SIGNATURES;

public class SootSetup {

	public static void initSootInstance(File apk, String outputDirectory, String androidPlatforms) {
		System.out.println("Init soot instance with apk located at: " + apk.getAbsolutePath());
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_validate(true);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_android_jars(androidPlatforms);
		List<String> dexToLoad = new ArrayList<>();
		dexToLoad.add(apk.getAbsolutePath());
		if(Configuration.v().getSootInstrumentationConfiguration().isInstrumentApkWithSoot()) {
			dexToLoad.add(Configuration.v().getSootInstrumentationConfiguration().getGroomPath());
		}
		Options.v().set_verbose(true);
		Options.v().set_process_dir(dexToLoad);
		Options.v().set_process_multiple_dex(true);
		Options.v().set_whole_program(true);
		Options.v().set_output_dir(outputDirectory);
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_force_overwrite(true);
//		Options.v().set_prepend_classpath(true);
//		Options.v().set_soot_classpath("/Users/lgitzing/Development/work/InjectedLogger/out/production/classes/InjectedHelper.dex");
//			Options.v().set_output_format(Options.output_format_jimple);
		System.out.println("Load necessary classes.");
//		Scene.v().addBasicClass("InjectedHelper");
		Scene.v().loadClassAndSupport("Groom");
//		Scene.v().loadClass("InjectedHelper", SIGNATURES);
		Scene.v().addBasicClass("android.util.Log", SIGNATURES);
//		Scene.v().loadClass("android.content.Context", SIGNATURES);
//		Scene.v().loadClass("android.app.Service", SIGNATURES);
		Scene.v().loadNecessaryClasses();
		new File(outputDirectory + "/" + apk.getName());
	}
}
