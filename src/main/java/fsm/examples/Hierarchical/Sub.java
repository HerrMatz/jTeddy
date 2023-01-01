package fsm.examples.Hierarchical;

import java.util.List;

public class Sub extends MyState {

	public static class Normal extends MyState {
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

		public Normal(MyState from) {
			super(from);
			TRANSITION(Event.up, (payload -> ENTER(new High(this))));
		}
	}

	public static class Low extends MyState {
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

		public Low(MyState from) {
			super(from);
			TRANSITION(Event.up, (payload -> ENTER(new High(this))));
			TRANSITION(Event.clear, (payload -> ENTER(new Normal(this))));
			TRANSITION(Event.exceed, (payload -> EXIT()));
			TRANSITION(Event.last, (payload -> EXIT(new FSM.C((Sub)parent))));
		}
	}

	public static class High extends MyState {
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

		public High(MyState from) {
			super(from);
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
	public MyState defaultExit() {
		return new FSM.D(this);
	}

	public Sub(MyState from) {
		super(from);
		TRANSITION(Event.reset, (payload -> ENTER(new Sub(this))));
		TRANSITION(Event.error, (payload -> ENTER(new FSM.E(this))));
		TRANSITION(Event.tick, (payload -> ENTER_DEEP(new Sub(this))));
		TRANSITION(Event.shallow, (payload -> ENTER_SHALLOW(new Sub(this))));
	}
	@Override
	protected List<MyState> defaultEntry() {
		return List.of(new Normal(null));
	}

}
