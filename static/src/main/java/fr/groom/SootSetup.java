package fr.groom;

import static soot.SootClass.BODIES;
import static soot.SootClass.SIGNATURES;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.groom.apk_instrumentation.InstrumenterUtils;
import soot.Scene;
import soot.options.Options;

public class SootSetup {

	public static void initSootInstance(File apk, String outputDirectory, String androidPlatforms) {
		System.out.println("Init soot instance with apk located at: " + apk.getAbsolutePath());
		Options.v().set_ignore_resolution_errors(true);
		Options.v().set_wrong_staticness(Options.wrong_staticness_ignore);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_validate(true);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_android_jars(androidPlatforms);
		List<String> dexToLoad = new ArrayList<>();
		dexToLoad.add(apk.getAbsolutePath());
		if (Configuration.v().getSootInstrumentationConfiguration().isInstrumentApkWithSoot()) {
			dexToLoad.add(Configuration.v().getSootInstrumentationConfiguration().getGroomPath());
		}
		Options.v().set_verbose(true);
		Options.v().set_process_dir(dexToLoad);
		Options.v().set_process_multiple_dex(true);
		Options.v().set_whole_program(true);
		Options.v().set_output_dir(outputDirectory + "/sootOutput");
		Options.v().set_output_format(Options.output_format_dex);
		Options.v().set_force_overwrite(true);
		Options.v().set_include_all(true);

		// Triggers targetSdkVersion looking in AndroidManifest file to estimate compilation SDK
		Scene.v().getAndroidJarPath(Options.v().android_jars(), apk.getAbsolutePath());
		int choosenApiVersion = Scene.v().getAndroidAPIVersion();
		if (choosenApiVersion > 23) {
			Options.v().set_force_android_jar(Configuration.v().getSootConfiguration().getAndroidPlatforms() + "/android-23/android.jar");
		}




//		Options.v().set_prepend_classpath(true);
//		Options.v().set_soot_classpath("/Users/lgitzing/Development/work/InjectedLogger/out/production/classes/InjectedHelper.dex");
//			Options.v().set_output_format(Options.output_format_jimple);
		System.out.println("Load necessary classes.");

//		Scene.v().addBasicClass("InjectedHelper");
		Scene.v().loadClassAndSupport("Groom");
//		Scene.v().loadClass("InjectedHelper", SIGNATURES);
		Scene.v().addBasicClass("java.lang.Object",BODIES);
		Scene.v().addBasicClass("java.lang.RuntimeException",BODIES);
		Scene.v().addBasicClass("java.lang.Exception",BODIES);
		Scene.v().addBasicClass("android.util.Log", SIGNATURES);
		Scene.v().addBasicClass("java.lang.Throwable",BODIES);
		Scene.v().addBasicClass("java.lang.NullPointerException",BODIES);
		Scene.v().addBasicClass("android.os.BaseBundle",BODIES);
		Scene.v().addBasicClass("java.lang.Boolean",BODIES);
		Scene.v().addBasicClass("java.util.concurrent.atomic.AtomicInteger",BODIES);
		Scene.v().addBasicClass("java.lang.Number",BODIES);
		Scene.v().addBasicClass("java.lang.Integer",BODIES);

		Scene.v().addBasicClass("android.content.ContentResolver",BODIES);
		Scene.v().addBasicClass("android.text.TextUtils",BODIES);
		Scene.v().addBasicClass("android.content.Intent",BODIES);
		Scene.v().addBasicClass("java.lang.String",BODIES);
		Scene.v().addBasicClass("java.util.HashMap",BODIES);
		Scene.v().addBasicClass("java.util.AbstractMap",BODIES);
		Scene.v().addBasicClass("java.util.ArrayList",BODIES);
		Scene.v().addBasicClass("java.util.AbstractList",BODIES);
		Scene.v().addBasicClass("java.util.AbstractCollection",BODIES);
		Scene.v().addBasicClass("android.os.Handler",BODIES);
		Scene.v().addBasicClass("java.util.concurrent.atomic.AtomicBoolean",BODIES);
		Scene.v().addBasicClass("java.util.Date",BODIES);
		Scene.v().addBasicClass("java.lang.StringBuilder",BODIES);
		Scene.v().addBasicClass("android.util.Log",BODIES);
		Scene.v().addBasicClass("java.lang.AbstractStringBuilder",BODIES);
		Scene.v().addBasicClass("android.content.IntentFilter",BODIES);
		Scene.v().addBasicClass("java.lang.Thread",BODIES);
		Scene.v().addBasicClass("java.lang.Enum",BODIES);
		Scene.v().addBasicClass("java.lang.StackTraceElement",BODIES);
		Scene.v().addBasicClass("java.lang.IllegalArgumentException",BODIES);
		Scene.v().addBasicClass("java.lang.Class",BODIES);
		Scene.v().addBasicClass("java.util.UUID",BODIES);
		Scene.v().addBasicClass("java.lang.IllegalStateException",BODIES);










		
//		Scene.v().loadClass("android.content.Context", SIGNATURES);
//		Scene.v().loadClass("android.app.Service", SIGNATURES);

		Scene.v().loadNecessaryClasses();
		new File(outputDirectory + "/" + apk.getName());
		System.out.println("##########################Testing the problematic Method");
		InstrumenterUtils.print_problematic_method();

//		InfoflowAndroidConfiguration configuration = new InfoflowAndroidConfiguration();
//		configuration.setSootIntegrationMode(InfoflowAndroidConfiguration.SootIntegrationMode.UseExistingInstance);
//		configuration.getAnalysisFileConfig().setTargetAPKFile(apk.getAbsolutePath());
//		configuration.getAnalysisFileConfig().setAndroidPlatformDir(androidPlatforms);
//		configuration.getAnalysisFileConfig().validate();
//		SetupApplication app = new SetupApplication(configuration);
//		app.setCallbackFile(Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().getFlowDroidInputFiles().getAndroidCallbacks());
//		System.out.println("Start constructing call graph");
//		app.constructCallgraph();
//		System.out.println("done");
	}
}
