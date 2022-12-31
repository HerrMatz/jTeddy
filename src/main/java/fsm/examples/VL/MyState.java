package fsm.examples.VL;

import fsm.base.State;

public class MyState extends State<Event, Integer, Object> {

	public MyState(MyState other) {
		super(other, Event.class);
	}
	
	public MyState(MyState from, MyState entry) {
		super(from, entry, Event.class);
	}
	
}
