package fr.groom.static_models;

import soot.jimple.infoflow.android.data.CategoryDefinition;
import soot.jimple.infoflow.android.data.parsers.CategorizedAndroidSourceSinkParser;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinition;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider;
import soot.jimple.infoflow.sourcesSinks.definitions.SourceSinkType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CategorizedSourceSinkDefinitionProvider implements ISourceSinkDefinitionProvider {
	private Set<ISourceSinkDefinition> sources;
	private Set<ISourceSinkDefinition> sinks;

	public CategorizedSourceSinkDefinitionProvider(Set<ISourceSinkDefinition> sources, Set<ISourceSinkDefinition> sinks) {
		this.sources = sources;
		this.sinks = sinks;
	}

	public CategorizedSourceSinkDefinitionProvider(String sourceFile, String sinkFile) {
			CategoryDefinition allCats = new CategoryDefinition(CategoryDefinition.CATEGORY.ALL);
			Set<CategoryDefinition> categories = new HashSet<>();
			categories.add(allCats);

			CategorizedAndroidSourceSinkParser sourceParser = new CategorizedAndroidSourceSinkParser(categories, sourceFile, SourceSinkType.Source);
		try {
			this.sources = sourceParser.parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CategorizedAndroidSourceSinkParser sinkParser = new CategorizedAndroidSourceSinkParser(categories, sinkFile, SourceSinkType.Sink);
		try {
			this.sinks = sinkParser.parse();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<ISourceSinkDefinition> getSources() {
		return this.sources;
	}

	@Override
	public Set<ISourceSinkDefinition> getSinks() {
		return this.sinks;
	}

	@Override
	public Set<ISourceSinkDefinition> getAllMethods() {
		Set<ISourceSinkDefinition> all = this.sources;
		all.addAll(this.sinks);
		return all;
	}
}
