package fsm.examples.Complex2;

public class FSM extends MyState{

	public FSM() {
		super(null);
		start(new Init(null), new SimpleContextData());
	}

	public static class Init extends MyState {
		public Init(MyState from) {
			super(from);
			TRANSITION(Event.start, (e -> ENTER(new Active(this))));
		}
	}

	public static class Inactive extends MyState {
		public Inactive(MyState from) {
			super(from);
			TRANSITION(Event.deep, (e -> ENTER_DEEP(new Active(this))));
			TRANSITION(Event.shallow, (e -> ENTER_SHALLOW(new Active(this))));
		}
	}
	
}
