package fsm.examples.Complex;

import fsm.base.State;
import fsm.base.Superstate;

public class Complex extends Superstate<Event> {

	public Complex() {
		super(null, Event.class);
		//TODO Auto-generated constructor stub
		start(new Init(null));
	}

	public static class Init extends State<Event> {
		public Init(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.start, (e -> ENTER(new Active(this))));
		}
	}
	
	public static class Inactive extends State<Event> {
		public Inactive(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.deep, (e -> ENTER_DEEP(new Active(this))));
			// TRANSITION(Event.shallow, (e -> ENTER_SHALLOW(new Active(this))));
		}
	}
	
}
