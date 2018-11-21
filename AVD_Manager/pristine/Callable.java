package fr.groom.remoting;

public interface Callable<V,T extends Throwable> {
	V call() throws T;
}
