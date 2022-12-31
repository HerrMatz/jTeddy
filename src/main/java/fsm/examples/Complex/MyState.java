package fsm.examples.Complex;

import fsm.base.State;

public class MyState extends State<Event, Integer, SimpleContextData> {

	public MyState(MyState other) {
		super(other, Event.class);
	}
	
}
