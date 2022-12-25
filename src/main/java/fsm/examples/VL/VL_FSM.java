package fsm.examples.VL;

import fsm.base.State;
import fsm.base.Superstate;

public class VL_FSM extends Superstate<Event> {

	public StringBuilder data = new StringBuilder();

	public static class A extends State<Event> {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iA");
		}
		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oA");
		}
		@Override
		public void pauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("pA");
		}
		@Override
		public void unpauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("uA");
		}

		public A(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.start, (payload -> ENTER(new Sub(this))));
			TRANSITION(Event.bypass, (payload -> ENTER(new B(this))));
		}
	}

	public static class B extends State<Event> {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iB");
		}
		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oB");
		}
		@Override
		public void pauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("pB");
		}
		@Override
		public void unpauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("uB");
		}

		public B(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.up, (payload -> ENTER(new Sub(this, new Sub.High(null)))));
		}
	}

	public static class C extends State<Event> {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iC");
		}
		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oC");
		}
		@Override
		public void pauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("pC");
		}
		@Override
		public void unpauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("uC");
		}

		public C(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.bypass, (payload -> ENTER(new D(this))));
			TRANSITION(Event.toF, (payload -> ENTER(new F(this))));
		}
	}

	public static class D extends State<Event> {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iD");
		}
		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oD");
		}
		@Override
		public void pauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("pD");
		}
		@Override
		public void unpauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("uD");
		}

		public D(State<Event> from) {
			super(from, Event.class);
		}
	}

	public static class E extends State<Event> {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iE");
		}
		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oE");
		}
		@Override
		public void pauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("pE");
		}
		@Override
		public void unpauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("uE");
		}

		public E(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.toF, (payload -> ENTER(new F(this))));
		}
	}

	public static class F extends State<Event> {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iF");
		}
		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oF");
		}
		@Override
		public void pauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("pF");
		}
		@Override
		public void unpauseAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("uF");
		}

		public F(State<Event> from) {
			super(from, Event.class);
			TRANSITION(Event.clear, (payload -> ENTER_DEEP(new Sub(null))));
		}
	}

	public VL_FSM() {
		super(null, null, Event.class);
		// setPauseActionIsExitAction(true);
		// setUnpauseActionIsEntryAction(true);
		start(new A(null));
	}
}
