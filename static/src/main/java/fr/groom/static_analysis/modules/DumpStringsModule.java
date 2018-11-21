package fr.groom.static_analysis.modules;

import fr.groom.Main;
import fr.groom.static_analysis.StaticAnalysis;
import org.json.JSONObject;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.List;

public class DumpStringsModule extends Module<List<String>> implements IModule {
	private String storageField = "strings";
	public DumpStringsModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.UNITLEVEL, staticAnalysis);
	}


	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		Stmt stmt = (Stmt) unit;
		stmt.apply(new AbstractStmtSwitch() {
			@Override
			public void caseInvokeStmt(InvokeStmt invokeStmt) {
				List<String> argStrings = extractStringsFromArgs(invokeStmt.getInvokeExpr().getArgs());
				for (String argString : argStrings) {
					resultHandler(argString);
				}
			}

			@Override
			public void caseAssignStmt(AssignStmt assignStmt) {
				assignStmt.getRightOp().apply(new AbstractJimpleValueSwitch() {
					@Override
					public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
						List<String> argStrings = extractStringsFromArgs(v.getArgs());
						for (String argString : argStrings) {
							resultHandler(argString);
						}
					}

					@Override
					public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
						List<String> argStrings = extractStringsFromArgs(v.getArgs());
						for (String argString : argStrings) {
							resultHandler(argString);
						}
					}

					@Override
					public void caseStaticInvokeExpr(StaticInvokeExpr v) {
						List<String> argStrings = extractStringsFromArgs(v.getArgs());
						for (String argString : argStrings) {
							resultHandler(argString);
						}
					}

					@Override
					public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
						List<String> argStrings = extractStringsFromArgs(v.getArgs());
						for (String argString : argStrings) {
							resultHandler(argString);
						}
					}

					@Override
					public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
						List<String> argStrings = extractStringsFromArgs(v.getArgs());
						for (String argString : argStrings) {
							resultHandler(argString);
						}
					}

					@Override
					public void caseStringConstant(StringConstant v) {
						resultHandler(v.toString());
					}
				});
			}
		});
	}

	private static List<String> extractStringsFromArgs(List<Value> args) {
		List<String> strings = new ArrayList<>();
		for (Value arg : args) {
			Type argType = arg.getType();
			if (argType.toString().equals("java.lang.String")) {
				if (argType instanceof RefType) {
					if (arg instanceof StringConstant) {
						StringConstant stringConstant = (StringConstant) arg;
						strings.add(stringConstant.toString());
					}
				}
			}
		}
		return strings;
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		field.put(this.storageField, this.data);
		dataUpdate.put("$set", field);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, dataUpdate, Main.STATIC_COLLECTION);
	}

	@Override
	public void resultHandler(Object result) {
		String string = (String) result;
		if (!this.data.contains(string)) {
			this.data.add((String) result);
		}
		int batchLimit = 100;
		if (this.data.size() == batchLimit) {
//			this.dataHandler.updateAnalysis(getFinalDocument());
			this.data = new ArrayList<>();
		}
	}

	@Override
	public void onFinish() {
		saveResults();
	}
}
