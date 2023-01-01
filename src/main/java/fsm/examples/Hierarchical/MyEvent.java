package fsm.examples.Hierarchical;

import fsm.base.BaseEvent;

public class MyEvent extends BaseEvent<Event, Integer> {

	public MyEvent(Event e, Integer p) {
		super(e, p);
	}
	
}
