package fsm.examples.Parallel;

public class FSM extends MyState {

	public FSM() {
		super(null);
		setPauseActionIsExitAction(false);
		setUnpauseActionIsEntryAction(false);
		start(new Init(null), new SimpleContextData());
	}

	public static class Init extends MyState {
		public Init(MyState from) {
			super(from);
			TRANSITION(Event.start, (e -> {
				contextData.i = e;
				return ENTER(new Active(this));
			}));
		}
		@Override
		protected void entryAction() {
			contextData.s.append("iI");
		}
		@Override
		protected void exitAction() {
			contextData.s.append("oI");
		}
	}

	public static class Inactive extends MyState {
		public Inactive(MyState from) {
			super(from);
			TRANSITION(Event.start, (e -> ENTER(new Active(this))));
			TRANSITION(Event.deep, (e -> ENTER_DEEP(new Active(this))));
			TRANSITION(Event.shallow, (e -> ENTER_SHALLOW(new Active(this))));
			TRANSITION(Event.exit, (e -> EXIT()));
		}
	}
	
}
