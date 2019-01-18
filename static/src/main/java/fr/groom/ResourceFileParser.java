package fr.groom;

import org.apache.commons.io.IOUtils;
import soot.jimple.infoflow.android.resources.AbstractResourceParser;
import soot.jimple.infoflow.android.resources.IResourceHandler;
import sun.nio.ch.IOUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ResourceFileParser extends AbstractResourceParser {
	public byte[] getAppIcon(final String fileName, Set<String> filters) {
		List<byte[]> icons = new ArrayList<>();
		handleAndroidResourceFiles(fileName, /* classes, */ filters, new IResourceHandler() {

			@Override
			public void handleResourceFile(final String fileName, Set<String> fileNameFilter, InputStream stream) {
				if (fileNameFilter.contains(fileName)) {
					try {
						byte[] bytes = IOUtils.toByteArray(stream);
						icons.add(bytes);
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

	public HashSet<String> getAbis(final String fileName) {
		HashSet<String> abis = new HashSet<>();
		HashSet<String> filters = new HashSet<>();
		filters.add("armeabi-v7a");
		filters.add("x86");
		filters.add("arm64-v8a");
		filters.add("armeabi");
		filters.add("arm64");
		filters.add("x86_64");
		handleAndroidResourceFiles(fileName, filters, new IResourceHandler() {
			@Override
			public void handleResourceFile(String fileName, Set<String> fileNameFilter, InputStream stream) {
				for(String filter: fileNameFilter) {
					String path = "lib/" + filter;
					if (fileName.contains(path)) {
						abis.add(filter);
					}
				}
			}
		});
		return abis;
	}
}
