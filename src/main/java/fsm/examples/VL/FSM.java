package fsm.examples.VL;

public class FSM extends MyState {

	public StringBuilder data = new StringBuilder();
	public StringBuilder data2 = new StringBuilder();

	public static class A extends MyState {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iA");
			((FSM)parent).data2.append("iA");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oA");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pA");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uA");
		}

		public A(MyState from) {
			super(from);
			TRANSITION(Event.start, (payload -> ENTER(new Sub(this))));
			TRANSITION(Event.toB, (payload -> ENTER(new B(this))));
		}
	}

	public static class B extends MyState {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iB");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oB");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pB");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uB");
		}

		public B(MyState from) {
			super(from);
			TRANSITION(Event.up, (payload -> ENTER(new Sub(this, new Sub.High(null)))));
		}
	}

	public static class C extends MyState {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iC");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oC");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pC");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uC");
		}

		public C(MyState from) {
			super(from);
			TRANSITION(Event.toD, (payload -> ENTER(new D(this))));
			TRANSITION(Event.toF, (payload -> ENTER(new F(this))));
		}
	}

	public static class D extends MyState {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iD");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oD");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pD");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uD");
		}

		public D(MyState from) {
			super(from);
		}
	}

	public static class E extends MyState {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iE");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oE");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pE");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uE");
		}

		public E(MyState from) {
			super(from);
			TRANSITION(Event.toF, (payload -> ENTER(new F(this))));
		}
	}

	public static class F extends MyState {
		@Override
		public void entryAction() {
			get(FSM.class, StringBuilder.class, "data").append("iF");
		}
		@Override
		public void exitAction() {
			get(FSM.class, StringBuilder.class, "data").append("oF");
		}
		@Override
		public void pauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("pF");
		}
		@Override
		public void unpauseAction() {
			get(FSM.class, StringBuilder.class, "data").append("uF");
		}

		public F(MyState from) {
			super(from);
			TRANSITION(Event.deep, (payload -> ENTER_DEEP(new Sub(null))));
			TRANSITION(Event.shallow, (payload -> ENTER_SHALLOW(new Sub(null))));
		}
	}

	public FSM() {
		super(null);
		// setPauseActionIsExitAction(true);
		// setUnpauseActionIsEntryAction(true);
		start(new A(null));
	}
}
