package fr.groom.static_analysis;

import org.json.JSONArray;
import org.json.JSONObject;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.data.CategoryDefinition;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.sourcesSinks.definitions.SourceSinkDefinition;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.List;

public class FlowDroidResultsHandler implements ResultsAvailableHandler {
	private IInfoflowCFG lastICFG = null;

	@Override
	public void onResultsAvailable(IInfoflowCFG cfg, InfoflowResults results) {
		this.lastICFG = cfg;
	}

	public JSONArray getResultsAsDocument(InfoflowResults results) {
		JSONArray sinks = new JSONArray();
		if (results.getResults() != null) {
			for (ResultSinkInfo sink : results.getResults().keySet()) {
				SourceSinkDefinition sinkDefinition = sink.getDefinition();
				JSONObject sinkObject = new JSONObject();
				sinkObject.put("signature", sink.toString());
				CategoryDefinition sinkCategoryDef = (CategoryDefinition) sinkDefinition.getCategory();
				String sinkCategory;
				if (sinkDefinition.getCategory() == null) {
					sinkCategory = "No Category";
				} else if (sinkCategoryDef.getCustomCategory() != null) {
					sinkCategory = sinkCategoryDef.getCustomCategory();
				} else {
					sinkCategory = sinkDefinition.getCategory().getHumanReadableDescription();
				}
				sinkObject.put("category", sinkCategory);
				JSONArray sources = new JSONArray();
				for (ResultSourceInfo source : results.getResults().get(sink)) {
					SourceSinkDefinition sourceDefinition = source.getDefinition();
					JSONObject sourceObject = new JSONObject();
					sourceObject.put("source", source.toString());
					String sourceCategory;
					if (sourceDefinition.getCategory() == null) {
						sourceCategory = "No Category";
					} else {
						sourceCategory = sourceDefinition.getCategory().getHumanReadableDescription();
					}
					sourceObject.put("category", sourceCategory);
					Stmt statement = source.getStmt();
					if (statement.containsInvokeExpr()) {
						InvokeExpr invokeExpr = statement.getInvokeExpr();

						SootMethod sootMethod = this.lastICFG.getMethodOf((Unit) source.getStmt());
						JSONArray arguments = new JSONArray();
						for (int i = 0; i < invokeExpr.getArgs().size(); i++) {
							Value arg = invokeExpr.getArg(i);
							List<String> argumentDefinitions = new ArrayList<>();
							if (arg instanceof Local) {
								Local local = (Local) arg;
								Chain<Unit> defs = BackwardAnalysis.getUnitDefinitions(sootMethod, statement, local);
								for (Unit def : defs) {
									argumentDefinitions.add(def.toString());
								}
							} else {
								argumentDefinitions.add(arg.toString());
							}
							JSONObject argument = new JSONObject();
							argument.put("arg" + i, argumentDefinitions);
							arguments.put(argument);
						}
						sourceObject.put("arguments", arguments);
					}
					sources.put(sourceObject);
				}
				sinkObject.put("sources", sources);
				sinks.put(sinkObject);
			}
		}
		return sinks;
	}
}
