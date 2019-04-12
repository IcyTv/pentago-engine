package core.event;

import java.util.ArrayList;
import java.util.List;

public class Queue {
	private List<Callback> callbackQueue;

	public Queue() {
		callbackQueue = new ArrayList<Callback>();
	}

	public boolean isEmpty() {
		return callbackQueue.isEmpty();
	}

	public Callback poll() {
		return callbackQueue.remove(0);
	}

	public void put(Callback c) {
		callbackQueue.add(c);
	}
}