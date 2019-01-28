package fr.groom.models;

import fr.groom.Configuration;
import fr.groom.Main;
import fr.groom.ResourceFileParser;
import fr.groom.utils.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.infoflow.android.resources.ARSCFileParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Application {
	public static int MAX_API_LEVEL = 25;

	private String sha256;
	private String sha1;
	private String md5;
	private String versionName;
	private long size;
	private byte[] iconBytes;
	private boolean isWebview;
	private int minAPILevel;
	private int targetSdkVersion;
	private HashMap<String, byte[]> assets;
	private HashSet<String> abis;
	private Set<String> permissions;
	private String packageName;
	private SootClass mainActivity;
	private File apk;
	private File sootInstrumentedApk;
	private File fridaInstrumentedApk;
	private File alignedApk;
	private File signedApk;
	private File lastEditedApk;
	private File finalApk;
	private ProcessManifest manifest;
	private HashSet<IComponent> components;
	private boolean isRunning;
	private String description;
	private String legacyFilename;


	public Application(File apk) {
		System.out.println("Init Application instance with apk located at:" + apk.getAbsolutePath());
		this.apk = apk;
		this.legacyFilename = apk.getName();
		this.manifest = Application.getManifest(apk.getAbsolutePath());
		if (!this.apk.exists() || this.manifest == null) {
			System.err.println("Invalid apk path or no manifest");
			System.exit(1);
		}
		setLastEditedApk(apk);
		this.isRunning = false;
		this.description = Configuration.v().getDescription();
		this.sha256 = FileUtils.createSha256(apk);
		this.sha1 = FileUtils.createSha1(apk);
		this.md5 = FileUtils.createMd5(apk);
		this.versionName = manifest.getVersionName();
		this.size = new File(manifest.getApk().getAbsolutePath()).length();
		this.iconBytes = extractApplicationIcon(manifest);
		this.abis = getAbis(apk);
		this.minAPILevel = manifest.getMinSdkVersion();
		this.targetSdkVersion = manifest.targetSdkVersion();
		this.permissions = manifest.getPermissions();
		this.assets = extractAssets(this.apk.getAbsolutePath());
		this.packageName = this.manifest.getPackageName();

		ArrayList<SootClass> launchableActivities = this.getLaunchableActivitySootClasses();
		if (launchableActivities.isEmpty()) {
			System.err.println("Application should have a launchable activity");
			System.exit(1);
		}
		this.mainActivity = launchableActivities.get(0);

		this.components = new HashSet<>();
		this.components.addAll(this.extractComponents("activity", this.manifest.getAllActivities()));
		this.components.addAll(this.extractComponents("service", this.manifest.getServices()));
		this.components.addAll(this.extractComponents("receiver", this.manifest.getReceivers()));
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}


	public static ProcessManifest getManifest(String pathToApk) {
		try {
			return new ProcessManifest(pathToApk);
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void setSootInstrumentedApk(File sootInstrumentedApk) {
		this.sootInstrumentedApk = sootInstrumentedApk;
		setLastEditedApk(this.sootInstrumentedApk);
	}

	public String getPackageName() {
		return packageName;
	}

	public SootClass getMainActivity() {
		return mainActivity;
	}

	public ProcessManifest getManifest() {
		return manifest;
	}

	public void setFridaInstrumentedApk(File fridaInstrumentedApk) {
		this.fridaInstrumentedApk = fridaInstrumentedApk;
		setLastEditedApk(this.fridaInstrumentedApk);
	}

	public void setAlignedApk(File alignedApk) {
		this.alignedApk = alignedApk;
		setLastEditedApk(this.alignedApk);
	}

	public void setSignedApk(File signedApk) {
		this.signedApk = signedApk;
		setLastEditedApk(this.signedApk);
	}

	public void setApk(File apk) {
		this.apk = apk;
		setLastEditedApk(this.apk);
	}

	public File getApk() {
		return apk;
	}


	public File getLastEditedApk() {
		return lastEditedApk;
	}

	private void setLastEditedApk(File lastEditedApk) {
		this.lastEditedApk = lastEditedApk;
	}

	public File getFinalApk() {
		return finalApk;
	}

	public void setFinalApk(File finalApk) {
		this.finalApk = finalApk;
		this.setLastEditedApk(this.finalApk);
	}

	public Set<IComponent> getComponents() {
		return components;
	}

	public IComponent getComponent(SootClass sootClass) {
		return components.stream().filter(c -> c.getSootClass().equals(sootClass)).findFirst().orElse(null);
	}

	public Set<Service> getServices() {
		return components.stream().filter(c -> c instanceof Service)
				.map(c -> (Service) c)
				.collect(Collectors.toSet());
	}

	public Set<Activity> getActivities() {
		return components.stream().filter(c -> c instanceof Activity)
				.map(c -> (Activity) c)
				.collect(Collectors.toSet());
	}

	public Set<Receiver> getReveivers() {
		return components.stream().filter(c -> c instanceof Receiver)
				.map(c -> (Receiver) c)
				.collect(Collectors.toSet());
	}

	private static SootClass getComponentClass(AXmlNode componentNode, String packageName) {
		String nameAttribute = componentNode.getAttribute("name").getValue().toString();
		String componentFullName = getComponentFullName(packageName, nameAttribute);
		return Scene.v().getSootClass(componentFullName);
	}

	private static String getComponentFullName(String packageName, String componentName) {
		int index = componentName.indexOf(".");
		StringBuilder retStr = new StringBuilder(packageName);
		if (index == 0) {
			retStr.append(componentName);
		} else if (index < 0) {
			retStr.append(".");
			retStr.append(componentName);
		} else {
			return componentName;
		}
		return retStr.toString();
	}

	public ArrayList<SootClass> getLaunchableActivitySootClasses() {
		ArrayList<SootClass> launchableActivitySootClasses = new ArrayList<>();
		Set<AXmlNode> launchableActivities = manifest.getLaunchableActivities();
		Iterator<AXmlNode> nodeIterator = launchableActivities.iterator();
		while (nodeIterator.hasNext()) {
			String activityName = getActivityName(nodeIterator.next());
			String activityFullName = getComponentFullName(manifest.getPackageName(), activityName);
			SootClass activity = Scene.v().getSootClass(activityFullName);
			launchableActivitySootClasses.add(activity);
		}
		return launchableActivitySootClasses;
	}

	private String getActivityName(AXmlNode activity) {
		AXmlAttribute attribute;
		if (ProcessManifest.isAliasActivity(activity)) {
			activity = this.manifest.getAliasActivityTarget(activity);
			if (activity == null)
				System.out.println("ici");
		}
		attribute = activity.getAttribute("name");
		return (String) attribute.getValue();
	}

	public static String retrieveStringFromArsc(int stringID, String pathToApk) {
		ARSCFileParser arscFileParser = new ARSCFileParser();
		try {
			arscFileParser.parse(pathToApk);
			List<ARSCFileParser.ResPackage> resPackages = arscFileParser.getPackages();
			for (ARSCFileParser.ResPackage resPackage : resPackages) {
				List<ARSCFileParser.ResType> resTypes = resPackage.getDeclaredTypes();
				for (ARSCFileParser.ResType resType : resTypes) {
					Collection<ARSCFileParser.AbstractResource> ressources = resType.getAllResources();
					ARSCFileParser.AbstractResource test = resType.getFirstResource(stringID);
					if (test != null) {
						if (test instanceof ARSCFileParser.StringResource) {
							return ((ARSCFileParser.StringResource) test).getValue();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private IntentFilter createIntentFilter(AXmlNode intentFilterNode) {
		IntentFilter intentFilter = new IntentFilter();
		for (AXmlNode actionNode : intentFilterNode.getChildrenWithTag("action")) {
			intentFilter.addAction((String) actionNode.getAttribute("name").getValue());
		}
		for (AXmlNode categoryNode : intentFilterNode.getChildrenWithTag("category")) {
			intentFilter.addCategory((String) categoryNode.getAttribute("name").getValue());
		}
		IntentFilterData intentFilterData = null;
		List<AXmlNode> dataSchemeNodes = intentFilterNode.getChildrenWithTag("data");
		if (!dataSchemeNodes.isEmpty()) {
			intentFilterData = new IntentFilterData();
		}

		for (AXmlNode dataSchemeNode : intentFilterNode.getChildrenWithTag("data")) {
			for (Map.Entry<String, AXmlAttribute<?>> aXmlAttributeMap : dataSchemeNode.getAttributes().entrySet()) {
				String tag = aXmlAttributeMap.getKey();
				aXmlAttributeMap.getValue().getValue();
				Object attributeValue = aXmlAttributeMap.getValue().getValue();
				String value;
				if (attributeValue instanceof Integer) {
					value = retrieveStringFromArsc((Integer) attributeValue, this.getLastEditedApk().getAbsolutePath());
				} else {
					value = (String) attributeValue;
				}
				switch (tag) {
					case "scheme":
						intentFilterData.addScheme(value);
						break;
					case "host":
						intentFilterData.addHost(value);
						break;
					case "port":
						intentFilterData.addPort(value);
						break;
					case "path":
						intentFilterData.addPath(value);
						break;
					case "pathPattern":
						intentFilterData.addPathPattern(value);
						break;
					case "pathPrefix":
						intentFilterData.addPathPrefix(value);
						break;
					case "mimeType":
						intentFilterData.addMimeType(value);
						break;
					default:
						System.out.println("ci");
				}
			}
		}
		if (intentFilterData != null) intentFilter.setIntentFilterData(intentFilterData);
		if (intentFilterNode.getAttribute("priority") != null) {
			intentFilter.setPriority((Integer) intentFilterNode.getAttribute("priority").getValue());
		}
		return intentFilter;
	}

	private ArrayList<Component> extractComponents(String componentType, List<AXmlNode> nodes) {
		AtomicInteger count = new AtomicInteger();
		ArrayList<Component> components = new ArrayList<>();
		nodes.forEach(node -> {
			List<AXmlNode> intentFiltersNode = node.getChildrenWithTag("intent-filter");
			List<IntentFilter> intentFilters = new ArrayList<>();
			for (AXmlNode intentFilterNode : intentFiltersNode) {
				IntentFilter intentFilter = createIntentFilter(intentFilterNode);
				intentFilters.add(intentFilter);
			}

			SootClass componentClass = getComponentClass(node, manifest.getPackageName());
			Component component = null;
			switch (componentType) {
				case "activity":
					component = new Activity(componentClass, count.get(), intentFilters);
					break;
				case "service":
					component = new Service(componentClass, count.get(), intentFilters);
					break;
				case "receiver":
					component = new Receiver(componentClass, count.get(), intentFilters);
					break;
			}
			components.add(component);
		});
		return components;
	}

	private static byte[] extractApplicationIcon(ProcessManifest manifest) {
		AXmlNode app = manifest.getApplication();
		AXmlAttribute attribute = app.getAttribute("icon");
		if (attribute != null) {
			Integer iconStringId = (Integer) attribute.getValue();
			ARSCFileParser resources = new ARSCFileParser();
			try {
				resources.parse(manifest.getApk().getAbsolutePath());
				List<ARSCFileParser.ResPackage> resPackages = resources.getPackages();
				for (ARSCFileParser.ResPackage resPackage : resPackages) {
					List<ARSCFileParser.ResType> resTypes = resPackage.getDeclaredTypes();
					for (ARSCFileParser.ResType resType : resTypes) {
						ARSCFileParser.AbstractResource test = resType.getFirstResource(iconStringId);
						if (test != null) {
							String fileName = ((ARSCFileParser.StringResource) test).getValue();
							ResourceFileParser resourceFileParser = new ResourceFileParser();
							Set<String> filter = new HashSet<>();
							filter.add(fileName);
							return resourceFileParser.getAppIcon(manifest.getApk().getAbsolutePath(), filter);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static HashSet<String> getAbis(File apk) {
		ResourceFileParser resourceFileParser = new ResourceFileParser();
		return resourceFileParser.getAbis(apk.getAbsolutePath());
	}

	private static HashMap<String, byte[]> extractAssets(String targetApkPath) {
		String string = "assets/";
		ResourceFileParser resourceFileParser = new ResourceFileParser();
		Set<String> filter = new HashSet<>();
		filter.add(string);
		return resourceFileParser.getAssetsFiles(targetApkPath, filter);
	}

	public String getSha256() {
		return sha256;
	}

	public int getMinAPILevel() {
		return minAPILevel;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		jo.put("legacy_filename", this.legacyFilename);
		jo.put("sha256", sha256);
		jo.put("sha1", sha1);
		jo.put("md5", md5);
		jo.put("version_name", versionName);
		jo.put("size", size);
		jo.put("icon", iconBytes);
		JSONArray abis = new JSONArray(this.abis);
		jo.put("abis", abis);
		jo.put("description", this.description);
		jo.put("min_api_level", minAPILevel);
		jo.put("target_sdk_version", targetSdkVersion);
		JSONArray assets = new JSONArray(this.assets.keySet());
		jo.put("assets", assets);
//		this.assets.forEach((fileName, fileBytes) -> {
//			JSONObject fileData = new JSONObject();
//			fileData.put("file_name", fileName);
//			fileData.put("bytes", fileBytes);
//			jo.accumulate("assets", fileData);
//		});
		jo.put("permissions", permissions);
		jo.put("package_name", packageName);
		jo.put("main_activity", this.mainActivity.getName());
		jo.put(Main.STATUS_KEY, "started");
		this.components.forEach(c -> jo.accumulate("components", c.toJson()));
		return jo;
	}



	public void setManifest(ProcessManifest manifest) {
		this.manifest = manifest;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}


//	public JSONObject toJson() {
//		JSONObject data = new JSONObject();
//		data.put("package_name", this.packageName);
//		data.put("version", this.versionName);
//		data.put("size", this.size);
//		data.put("icon_file", this.iconBytes);
//		data.put("isWebview", this.isWebview);
//		data.put("min_api_level", this.minAPILevel);
//		data.put("target_sdk_version", this.targetSdkVersion);
//		data.put("permissions", this.permissions);
//		data.put("activities", this.activities);
//		data.put("assets", this.assets);
//	}

//	public static ArrayList<SootClass> getRegisteredComponentsClasses(ProcessManifest manifest) {
//		ArrayList<AXmlNode> nodes = new ArrayList<>();
//		ArrayList<SootClass> componentSootClasses = new ArrayList<>();
//		nodes.addAll(manifest.getAllActivities());
//		nodes.addAll(manifest.getReceivers());
//		nodes.addAll(manifest.getServices());
//
//		nodes.forEach(node -> {
//			componentSootClasses.add(getComponentClass(node, manifest.getPackageName()));
//		});
//		return componentSootClasses;
//	}

//	public static ArrayList<SootClass> getReceiverSootClasses(ProcessManifest manifest) {
//		ArrayList<SootClass> receiverSootClasses = new ArrayList<>();
//		List<AXmlNode> receivers = manifest.getReceivers();
//		receivers.forEach(r -> {
//			String nameAttribute = r.getAttribute("name").getValue().toString();
//			String receiverFullName = getComponentFullName(manifest.getPackageName(), nameAttribute);
//			SootClass receiver = Scene.v().getSootClass(receiverFullName);
//			receiverSootClasses.add(receiver);
//		});
//		return receiverSootClasses;
//	}
//
}

