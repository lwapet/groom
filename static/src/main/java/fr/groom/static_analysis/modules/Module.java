package fr.groom.static_analysis.modules;

import fr.groom.Storage;
import fr.groom.static_analysis.StaticAnalysis;
import fr.groom.models.Application;

public abstract class Module<T> implements IModule {
	protected StaticAnalysis staticAnalysis;
	protected Application app;
	protected Storage storage;
	protected T data;
	private IModule.ModuleType levelType;

	Module(T data, IModule.ModuleType levelType, StaticAnalysis staticAnalysis) {
		this.data = data;
		this.levelType = levelType;
		this.staticAnalysis = staticAnalysis;
		this.app = staticAnalysis.getApp();
		this.storage = staticAnalysis.getStorage();
	}

	public T getData() {
		return data;
	}

	public IModule.ModuleType getLevelType() {
		return levelType;
	}
}
