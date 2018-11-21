package fr.groom.models;

public abstract class AbstractComponentSwitch implements ComponentSwitch{
	@Override
	public void caseActivity(Activity activity) {
		defaultCase(activity);
	}

	@Override
	public void caseService(Service service) {
		defaultCase(service);
	}

	@Override
	public void caseReceiver(Receiver receiver) {
		defaultCase(receiver);
	}

	@Override
	public void defaultCase(Object object) {

	}
}
