package fsm.examples.Hierarchical;

import fsm.base.State;

public abstract class MyState extends State<Event, Integer, Object> {

	public MyState(MyState other) {
		super(other, Event.class);
	}
	
}
