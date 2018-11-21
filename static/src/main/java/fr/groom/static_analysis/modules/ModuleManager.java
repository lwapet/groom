package fr.groom.static_analysis.modules;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	List<IModule> modules;

	public List<IModule> getModules() {
		return modules;
	}

	public ModuleManager() {
		this.modules = new ArrayList<>();
	}

	public IModule addModule(IModule module) {
		System.out.println("module : [" + module.getClass().getSimpleName() + "] added to ModuleManager");
		modules.add(module);
		return module;
	}

	public void removeModule(IModule module) {
		modules.remove(module);
	}

	public List<IModule> getModulesByLevel(IModule.ModuleType levelType) {
		List<IModule> filteredModules = new ArrayList<>();
		for (IModule module : modules) {
			if (module.getLevelType().equals(levelType)) {
				filteredModules.add(module);
			}
		}
		return filteredModules;
	}

	public <T extends IModule> T getModuleByType(Class<T> type) {
		List<T> list = new ArrayList<T>();
		for (IModule fruit : modules) {
			if (fruit.getClass() ==  type) {
				list.add(type.cast(fruit));
			}
		}
		return list.get(0);
	}
}
