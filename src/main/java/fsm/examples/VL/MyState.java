package fsm.examples.VL;

import fsm.base.State;

public abstract class MyState extends State<Event, Integer, Object> {

	public MyState(MyState other) {
		this(other, null);
	}
	
	public MyState(MyState from, MyState entry) {
		super(from, entry, Event.class);
	}
	
}
