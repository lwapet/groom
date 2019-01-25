package fr.groom.static_analysis;

import fr.groom.Main;
import fr.groom.Configuration;
import fr.groom.static_analysis.modules.*;
import fr.groom.Storage;
import fr.groom.models.Application;
import fr.groom.models.CategorizedSourceSinkDefinitionProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import soot.*;
import soot.jimple.infoflow.android.data.parsers.PermissionMethodParser;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StaticAnalysis extends SceneTransformer implements IAnalysis{
	private Application app;
	private Storage storage;
	private ModuleManager moduleManager;
	private List<String> sources;
	private List<String> sinks;
	private Timestamp executionStartTime;
	private Timestamp executionEndTime;
	private ISourceSinkDefinitionProvider provider;
	private JSONArray flowDroidResults;




	public StaticAnalysis(Application app, Storage storage) {
		this.app = app;
		this.storage = storage;
		this.moduleManager = new ModuleManager();
		this.sources = new ArrayList<>();
		this.sinks = new ArrayList<>();
		JSONObject data = new JSONObject();
		this.executionStartTime = new Timestamp(System.currentTimeMillis());
		setProvider();
//		IModule module = new DumpProtectedMethods(this);
//		this.moduleManager.addModule(module);
		this.moduleManager.addModule(new DumpClassModule(this));
		this.moduleManager.addModule(new DumpMethodModule(this));
//		this.moduleManager.addModule(module);
		if(Configuration.v().getStaticAnalysisConfiguration().isRunFlowDroid()) {
			FlowDroid flowDroid = new FlowDroid(this.app.getLastEditedApk(), provider);
			flowDroidResults = flowDroid.run();
		}
		IModule sourceModule = new DumpSourcesAndSinksModule(this);
		this.moduleManager.addModule(sourceModule);
//		IModule dumpRgeisterReciever = new DumpRegisterReceiverSpots(this);
//		this.moduleManager.addModule(dumpRgeisterReciever);
	}

	public void setProvider() {
		if (Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().getFlowDroidInputFiles().getSourcesAndSinksFiles().isCategorizedSourcesAndSinks()) {
			String categorizedSources = Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().getFlowDroidInputFiles().getSourcesAndSinksFiles().getCategorizedSourcesFile();
			String categorizedSinks = Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().getFlowDroidInputFiles().getSourcesAndSinksFiles().getCategorizedSinksFile();
			this.provider = new CategorizedSourceSinkDefinitionProvider(categorizedSources, categorizedSinks);
		} else {
			String sourceAndSinkFile = Configuration.v().getStaticAnalysisConfiguration().getFlowDroidConfiguration().getFlowDroidInputFiles().getSourcesAndSinksFiles().getSourceAndSinksTxtFile();
			try {
				this.provider = PermissionMethodParser.fromFile(sourceAndSinkFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleClassLevel(SootClass sootClass) {
		List<IModule> classLevelModules = this.moduleManager.getModulesByLevel(IModule.ModuleType.SOOTCLASSLEVEL);
		for (IModule module : classLevelModules) {
			module.executeModule(sootClass, null, null);
		}
	}

	private void handleMethodLevel(SootClass sootClass, SootMethod sootMethod) {
		List<IModule> methodLevelModules = this.moduleManager.getModulesByLevel(IModule.ModuleType.SOOTMETHODLEVEL);
		for (IModule module : methodLevelModules) {
			module.executeModule(sootClass, sootMethod, null);
		}
	}

	private void handleUnitLevel(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		List<IModule> classLevelModules = this.moduleManager.getModulesByLevel(IModule.ModuleType.UNITLEVEL);
		for (IModule module : classLevelModules) {
			module.executeModule(sootClass, sootMethod, unit);
		}
	}

	public Application getApp() {
		return app;
	}

	public Storage getStorage() {
		return storage;
	}

	public ISourceSinkDefinitionProvider getProvider() {
		return provider;
	}

	public void addSource(String source) {
		this.sources.add(source);
	}

	public void addSink(String sink) {
		this.sinks.add(sink);
	}

	private void onFinish() {
		this.executionEndTime = new Timestamp(System.currentTimeMillis());
		this.storeAnalysis();
	}

	@Override
	protected void internalTransform(String s, Map<String, String> map) {
		Iterator<SootClass> sootClassIterator = Scene.v().getClasses().snapshotIterator();
		while (sootClassIterator.hasNext()) {
			final SootClass sootClass = sootClassIterator.next();
			handleClassLevel(sootClass);
			List<SootMethod> clone = new ArrayList<>(sootClass.getMethods());
			for (SootMethod sootMethod : clone) {
				handleMethodLevel(sootClass, sootMethod);
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
						handleUnitLevel(sootClass, sootMethod, unit);
					}
				}
			}
		}
		this.moduleManager.getModules().forEach(IModule::onFinish);
		this.onFinish();
	}

	public List<String> getSources() {
		return sources;
	}

	public List<String> getSinks() {
		return sinks;
	}

	@Override
	public void storeAnalysis() {
		JSONObject data = new JSONObject();
		JSONArray sourcesArray = new JSONArray();
		sources.forEach(sourcesArray::put);

		JSONArray sinksArray = new JSONArray();
		sinks.forEach(sinksArray::put);

		data.put("sha256", this.app.getSha256());
		data.put("package_name", this.app.getPackageName());
		data.put("start_time", this.executionStartTime);
		data.put("end_time", this.executionEndTime);
		data.put("sources", sourcesArray);
		data.put("sinks", sinksArray);
		data.put("flowdroid_results", flowDroidResults);
		JSONObject filter = new JSONObject();
		filter.put("sha256", this.app.getSha256());
//		storage.insertData(app.toJson(), "application");
		this.storage.replace(filter, data, Main.STATIC_COLLECTION);
	}
}
