package fr.groom.models;

public interface ComponentSwitch extends Switch {
	public abstract void caseActivity(Activity activity);
	public abstract void caseService(Service service);
	public abstract void caseReceiver(Receiver receiver);
	public abstract void defaultCase(Object object);
}
