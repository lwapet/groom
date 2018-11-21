package fr.groom.static_analysis;

import fr.groom.Configuration;
import org.json.JSONArray;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

import java.io.File;
import java.io.IOException;

public class FlowDroid {
	private File apk;
	private ISourceSinkDefinitionProvider provider;

	public FlowDroid(File apk, ISourceSinkDefinitionProvider provider) {
		this.apk = apk;
		this.provider = provider;
	}

	private SetupApplication initFlowDroidInstance() {
		InfoflowAndroidConfiguration configuration = new InfoflowAndroidConfiguration();
		configuration.setSootIntegrationMode(InfoflowAndroidConfiguration.SootIntegrationMode.UseExistingInstance);
		configuration.getAnalysisFileConfig().setTargetAPKFile(this.apk.getAbsolutePath());
//		configuration.getAnalysisFileConfig().setSourceSinkFile(Config.sourcesAndSinksFile);
		configuration.getAnalysisFileConfig().setAndroidPlatformDir(Configuration.v().getSootConfiguration().getAndroidPlatforms());
		configuration.setLogSourcesAndSinks(true);
		if (!Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().isFlowSensitive())
			configuration.getSolverConfiguration().setDataFlowSolver(InfoflowConfiguration.DataFlowSolver.FlowInsensitive);
		if (!Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().isContextSensitive())
			configuration.getPathConfiguration().setPathBuildingAlgorithm(InfoflowConfiguration.PathBuildingAlgorithm.ContextInsensitive);
		configuration.setIgnoreFlowsInSystemPackages(false);
		configuration.getAnalysisFileConfig().validate();
		SetupApplication app = new SetupApplication(configuration);
		try {
			app.setCallbackFile(Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().getFlowDroidInputFiles().getAndroidCallbacks());
			EasyTaintWrapper easyTaintWrapper = new EasyTaintWrapper(Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().getFlowDroidInputFiles().getTaintWrapperSource());
			app.setTaintWrapper(easyTaintWrapper);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return app;
	}

	public JSONArray run() {
		System.out.println("Running FlowDroid");
		SetupApplication flowDroidApp = initFlowDroidInstance();
		FlowDroidResultsHandler resultsHandler = new FlowDroidResultsHandler();

		flowDroidApp.addResultsAvailableHandler(resultsHandler);

//			this.dataHandler.updateAnalysis(new Document("$set", new Document("status", analysis.getStatus())));

		InfoflowResults results = flowDroidApp.runInfoflow(provider);
		return resultsHandler.getResultsAsDocument(results);
	}
}
