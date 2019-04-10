package fr.groom;

import soot.jimple.infoflow.android.axml.ApkHandler;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

public class ApkHandlerGroom extends ApkHandler {
	public ApkHandlerGroom(String path) throws ZipException, IOException {
		super(path);
	}

	public ApkHandlerGroom(File apk) throws ZipException, IOException {
		super(apk);
	}


}
