package fr.groom.static_models;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;

import java.util.ArrayList;
import java.util.List;

public class JavascriptInterfaceMethod {
	private SootMethod method;
	private JavascriptInterface javascriptInterface;
	private ObjectId id;
	private List<Source> sources;

	public JavascriptInterfaceMethod(SootMethod method, JavascriptInterface javascriptInterface) {
		this.method = method;
		this.javascriptInterface = javascriptInterface;
		this.sources = new ArrayList<>();

		System.out.println("new JSIM analyzed : " + this.method.getSignature());
	}

	private static void getNextValue(SootMethod sootMethod, Unit unit, Value value) {
		if (value instanceof Local) {
			List<Unit> definitionUnits = getDefinitions(sootMethod, unit, (Local) value);
			for (Unit defUnit : definitionUnits) {
				if (defUnit instanceof AssignStmt) {
					AssignStmt assignStmt = (AssignStmt) unit;
				}
			}
		}
	}

	private static List<Unit> getDefinitions(SootMethod sootMethod, Unit unit, Local local) {
		List<Unit> definitionUnits = new ArrayList<>();
		if (sootMethod.isConcrete()) {
			CompleteUnitGraph unitGraph = new CompleteUnitGraph(sootMethod.retrieveActiveBody());
			SimpleLocalDefs simpleLocalDefs = new SimpleLocalDefs(unitGraph);
			definitionUnits = simpleLocalDefs.getDefsOfAt(local, unit);
		}
		return definitionUnits;
	}

	public SootMethod getMethod() {
		return method;
	}

	public JSONObject toJson() {
		JSONObject record = new JSONObject();
		this.id = new ObjectId();
		record.put("_id", this.id);
		record.put("signature", this.method.getSignature());
		record.put("body", this.method.retrieveActiveBody().toString());
		List<JSONObject> documentSources = new ArrayList<>();
		for (Source source : this.sources) {
			JSONObject documentSource = new JSONObject();
			documentSource.put("source_type", source.getSourceType());
			documentSource.put("source_signature", source.getSourceSignature());
			documentSource.put("history", source.getHistory());
			documentSources.add(documentSource);
		}
		record.append("sources", documentSources);

		return record;
	}


	public void saveSensitiveSourceFinding(List<String> history, String sourceType, String sourceSignature) {
		Source source = new Source(sourceType, sourceSignature, history);
		this.sources.add(source);
	}
}
