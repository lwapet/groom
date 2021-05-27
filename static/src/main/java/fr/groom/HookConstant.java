package fr.groom;

public enum HookConstant {
	MAIN_PATTERN("HOOK#"),
	PACKAGE_NAME(null),
	SHA_256(null),
	INTENT("INTENT"),
	COMPONENT_METHOD("COMPONENT_METHOD"),
	DATA_MARKER("__");

	private String value;

	HookConstant(String aValue) {
		value = aValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
