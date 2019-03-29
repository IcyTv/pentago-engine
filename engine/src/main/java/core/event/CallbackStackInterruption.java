package core.event;

public class CallbackStackInterruption extends Exception {

	private static final long serialVersionUID = 1L;

	public CallbackStackInterruption(String msg) {
		super(msg);
	}

}