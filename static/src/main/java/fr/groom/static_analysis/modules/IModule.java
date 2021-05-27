package fr.groom.static_analysis.modules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.HashMap;
import java.util.Map;

public interface IModule{

	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit);

	public IModule.ModuleType getLevelType();

	public void processResults();

	public void saveResults();

	public void resultHandler(Object result);

	public void onFinish();

	public enum ModuleType {

		SOOTCLASSLEVEL("sootClassLevel"),
		SOOTMETHODLEVEL("sootMethodLevel"),
		UNITLEVEL("unitLevel");
		private final String value;
		private final static Map<String, ModuleType> CONSTANTS = new HashMap<String, ModuleType>();

		static {
			for (IModule.ModuleType c: values()) {
				CONSTANTS.put(c.value, c);
			}
		}

		private ModuleType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		@JsonValue
		public String value() {
			return this.value;
		}

		@JsonCreator
		public static IModule.ModuleType fromValue(String value) {
			IModule.ModuleType constant = CONSTANTS.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}
}


