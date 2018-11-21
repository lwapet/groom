package fr.groom.android_emulator;

abstract class AndroidAVDException extends Exception {
	protected AndroidAVDException(String message) {
		super(message);
	}

	protected AndroidAVDException(String message, Throwable cause) {
		super(message, cause);
	}


}
final class AVDDiscoveryException extends AndroidAVDException {

	AVDDiscoveryException(String message) {
		super(message);
	}
}
