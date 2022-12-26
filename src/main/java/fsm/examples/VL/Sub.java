package fsm.examples.VL;

import fsm.base.State;
import fsm.base.Superstate;

public class Sub extends Superstate<Event> {

	public static class Normal extends State<Event> {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iN");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oN");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pN");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uN");
		}

		public Normal(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.up, (payload -> ENTER(new High(this))));
		}
	}

	public static class Low extends State<Event> {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iL");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oL");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pL");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uL");
		}

		public Low(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.up, (payload -> ENTER(new High(this))));
			TRANSITION(Event.clear, (payload -> ENTER(new Normal(this))));
			TRANSITION(Event.exceed, (payload -> EXIT()));
			TRANSITION(Event.last, (payload -> EXIT(new FSM.C(parent))));
		}
	}

	public static class High extends State<Event> {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iH");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oH");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pH");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uH");
		}

		public High(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.down, (payload -> ENTER(new Low(this))));
			TRANSITION(Event.exceed, (payload -> EXIT()));
		}
	}

	@Override
	public void entryAction() {
		get(FSM.class, StringBuilder.class, "data").append("iS");
	}
	@Override
	public void exitAction() {
		get(FSM.class, StringBuilder.class, "data").append("oS");
	}
	@Override
	public void pauseAction() {
		get(FSM.class, StringBuilder.class, "data").append("pS");
	}
	@Override
	public void unpauseAction() {
		get(FSM.class, StringBuilder.class, "data").append("uS");
	}

	@Override
	public State<Event> defaultExit() {
		return new FSM.D(this);
	}

	public Sub(State<Event> from, State<Event> entry) {
		super(from, entry, Event.class);
		TRANSITION(Event.reset, (payload -> ENTER(new Sub(this))));
		TRANSITION(Event.error, (payload -> ENTER(new FSM.E(this))));
	}

	public Sub(State<Event> from) {
		this(from, new Normal(null));
	}
}
