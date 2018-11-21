package fr.groom;

import soot.jimple.infoflow.android.resources.AbstractResourceParser;
import soot.jimple.infoflow.android.resources.IResourceHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ResourceFileParser extends AbstractResourceParser {
	public byte[] getAppIcon(final String fileName, Set<String> filters) {
		List<byte[]> icons = new ArrayList<>();
		handleAndroidResourceFiles(fileName, /* classes, */ filters, new IResourceHandler() {

			@Override
			public void handleResourceFile(final String fileName, Set<String> fileNameFilter, InputStream stream) {
				if (fileNameFilter.contains(fileName)) {
					try {
						icons.add(new byte[stream.available()]);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		if (!icons.isEmpty()) {
			return icons.get(0);
		} else {
			return null;
		}
	}

	public HashMap<String, byte[]> getAssetsFiles(final String fileName, Set<String> filters) {
		HashMap<String, byte[]> assets = new HashMap<>();

		handleAndroidResourceFiles(fileName, /* classes, */ filters, new IResourceHandler() {
			@Override
			public void handleResourceFile(final String fileName, Set<String> fileNameFilter, InputStream stream) {
				for (String filter : fileNameFilter) {
					if (fileName.contains(filter)) {
						try {
							byte[] buffer = new byte[stream.available()];
							assets.put(fileName, buffer);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});
		return assets;
	}
}
