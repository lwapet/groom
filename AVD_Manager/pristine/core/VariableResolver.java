package fr.groom.core;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.Collection;
import java.util.Map;

public interface VariableResolver<V> {
	VariableResolver NONE = new VariableResolver() {
		public Object resolve(String name) {
			return null;
		}
	};

	V resolve(String var1);

	public static final class Union<V> implements VariableResolver<V> {
		private final VariableResolver<? extends V>[] resolvers;

		public Union(VariableResolver... resolvers) {
			this.resolvers = (VariableResolver[])resolvers.clone();
		}

		public Union(Collection<? extends VariableResolver<? extends V>> resolvers) {
			this.resolvers = (VariableResolver[])resolvers.toArray(new VariableResolver[resolvers.size()]);
		}

		public V resolve(String name) {
			VariableResolver[] arr$ = this.resolvers;
			int len$ = arr$.length;

			for(int i$ = 0; i$ < len$; ++i$) {
				VariableResolver<? extends V> r = arr$[i$];
				V v = r.resolve(name);
				if (v != null) {
					return v;
				}
			}

			return null;
		}
	}

	public static final class ByMap<V> implements VariableResolver<V> {
		private final Map<String, V> data;

		public ByMap(Map<String, V> data) {
			this.data = data;
		}

		public V resolve(String name) {
			return this.data.get(name);
		}
	}
}

