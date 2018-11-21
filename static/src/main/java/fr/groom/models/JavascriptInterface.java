package fr.groom.models;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import soot.*;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;

import java.util.ArrayList;
import java.util.List;

public class JavascriptInterface {
	public static final String JSI_SIGNATURE = "<android.webkit.WebView: void addJavascriptInterface(java.lang.Object,java.lang.String)>";
	private SootMethod caller;
	private Unit addJSIUnit;
	private SootClass jsiClass;
	private String jsiName;
	private SootClass webkitClass;
	private ObjectId id;
	private List<JavascriptInterfaceMethod> javascriptInterfaceMethods;

	public JavascriptInterface(InvokeExpr invokeExpr, SootMethod caller, Unit addJSIUnit) {
		InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
		ValueBox baseBox = instanceInvokeExpr.getBaseBox();
		Value baseValue = baseBox.getValue();
		RefType baseType = (RefType) baseValue.getType();
		SootClass baseClass = baseType.getSootClass();

		Value jsiObject = invokeExpr.getArg(0); // jsiObject is the first argument
		Value jsiName = invokeExpr.getArg(1); // jsi name is the second argument

		RefType jsiType = (RefType) jsiObject.getType();
		SootClass jsiClass = jsiType.getSootClass();

		this.caller = caller;
		this.addJSIUnit = addJSIUnit;
		this.webkitClass = baseClass;
		this.jsiClass = jsiClass;
		this.jsiName = jsiName.toString();
		this.javascriptInterfaceMethods = new ArrayList<>();
		this.findInterfaceMethods();

//		this.jsiMethods = findInterfaceMethods();
	}

	public SootClass getJsiClass() {
		return jsiClass;
	}

	public SootMethod getCaller() {
		return caller;
	}

	public Unit getAddJSIUnit() {
		return addJSIUnit;
	}

	public List<JavascriptInterfaceMethod> getJavascriptInterfaceMethods() {
		return javascriptInterfaceMethods;
	}

	public ObjectId getId() {
		return id;
	}

	private void findInterfaceMethods() {
		for (SootMethod sootMethod : this.jsiClass.getMethods()) {
			for (Tag methodTag : sootMethod.getTags()) {
				if (methodTag instanceof VisibilityAnnotationTag) {
					VisibilityAnnotationTag vAnnotationTag = (VisibilityAnnotationTag) methodTag;
					List<AnnotationTag> annotationTags = vAnnotationTag.getAnnotations();
					for (AnnotationTag annotationTag : annotationTags) {
						if (annotationTag.getType().contains("JavascriptInterface") && sootMethod.isConcrete()) {
							JavascriptInterfaceMethod jsiMethod = new JavascriptInterfaceMethod(sootMethod, this);
							this.javascriptInterfaceMethods.add(jsiMethod);
						}
					}
				}
			}
		}
	}

	private void save() {

//		Analysis.v().updateAnalysis(update);
	}

	public JSONObject toJson() {
		this.id = new ObjectId();
		JSONObject record = new JSONObject();
		record.put("_id", this.id);
		record.put("name", this.jsiName);
		record.put("class", jsiClass.getName());
		List<JSONObject> jsimDocuments = new ArrayList<>();
		for (JavascriptInterfaceMethod jsim : this.getJavascriptInterfaceMethods()) {
			jsimDocuments.add(jsim.toJson());
		}
		record.put("javascript_interface_methods", jsimDocuments);
		return record;
	}

}
