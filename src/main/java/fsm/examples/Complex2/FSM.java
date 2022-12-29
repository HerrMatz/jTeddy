package fsm.examples.Complex2;

import fsm.base.State;
import fsm.base.Superstate;

public class FSM extends Superstate<Event> {

	public FSM() {
		super(null, Event.class);
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
			TRANSITION(Event.shallow, (e -> ENTER_SHALLOW(new Active(this))));
		}
	}
	
}
