package fsm.examples.Parallel2;

import java.util.List;

public class Active extends MyState {

	public StringBuilder data = new StringBuilder();

	public Active(MyState from) {
		super(from);
		TRANSITION(Event.exit, (e -> ENTER(new FSM.Inactive(this))));
		TRANSITION(Event.inner, (e -> APPEND(new SubN(null))));
		TRANSITION(Event.shallow, (e -> ENTER_SHALLOW(new Active(this))));
		TRANSITION(Event.deep, (e -> ENTER_DEEP(new Active(this))));
	}

	@Override
	protected MyState defaultExit() {
		return new FSM.Inactive(this);
	}
	@Override
	protected List<MyState> defaultEntry() {
		return List.of(new Sub1(null), new Sub2(null));
	}

	public static class Sub1 extends MyState {
		public Sub1(MyState from) {
			super(from);
		}

		@Override
		protected List<MyState> defaultEntry() {
			return List.of(new B(null));
		}

		public static class A extends MyState {
			public A(MyState from) {
				super(from);
				TRANSITION(Event.advance, (e -> EXIT()));
			}
		}

		public static class B extends MyState {
			public B(MyState from) {
				super(from);
			}

			@Override
			protected List<MyState> defaultEntry() {
				return List.of(new B1(null));
			}
	
			public static class B1 extends MyState {
				public B1(MyState from) {
					super(from);
					TRANSITION(Event.advance, (e -> ENTER(new B2(this))));
				}
			}

			public static class B2 extends MyState {
				public B2(MyState from) {
					super(from);
					TRANSITION(Event.advance, (e -> EXIT()));
				}
			}

			protected MyState defaultExit() {
				return new Sub1.A(this);
			}

		}
	}

	public static class Sub2 extends MyState {
		public Sub2(MyState from) {
			super(from);
		}

		@Override
		protected List<MyState> defaultEntry() {
			return List.of(new C(null));
		}

		public static class C extends MyState {
			public C(MyState from) {
				super(from);
				TRANSITION(Event.advance, (e -> ENTER(new D(this))));
				TRANSITION(Event.toD, (e -> ENTER(new D(this))));
			}
		}

		public static class D extends MyState {
			public D(MyState from) {
				super(from);
				TRANSITION(Event.advance, (e -> EXIT()));
			}
		}
	}

	public static class SubN extends MyState {
		public SubN(MyState from) {
			super(from);
		}

		@Override
		protected void entryAction() {
			((Active)parent).data.append("iS");
		}
		@Override
		protected void exitAction() {
			((Active)parent).data.append("oS");
		}
		@Override
		protected void pauseAction() {
			((Active)parent).data.append("pS");
		}
		@Override
		protected void unpauseAction() {
			((Active)parent).data.append("uS");
		}

		@Override
		protected List<MyState> defaultEntry() {
			return List.of(new E(null));
		}

		public static class E extends MyState {
			public E(MyState from) {
				super(from);
				TRANSITION(Event.advance, (e -> ENTER(new F(this))));
				TRANSITION(Event.toF, (e -> ENTER(new F(this))));
			}
			@Override
			protected void entryAction() {
				((Active)(((SubN)parent).parent)).data.append("iE");
			}
			@Override
			protected void exitAction() {
				((Active)(((SubN)parent).parent)).data.append("oE");
			}
			@Override
			protected void pauseAction() {
				((Active)(((SubN)parent).parent)).data.append("pE");
			}
			@Override
			protected void unpauseAction() {
				((Active)(((SubN)parent).parent)).data.append("uE");
			}
		}

		public static class F extends MyState {
			public F(MyState from) {
				super(from);
				TRANSITION(Event.endN, (e -> EXIT()));
			}
			@Override
			protected void entryAction() {
				((Active)(((SubN)parent).parent)).data.append("iF");
			}
			@Override
			protected void exitAction() {
				((Active)(((SubN)parent).parent)).data.append("oF");
			}
			@Override
			protected void pauseAction() {
				((Active)(((SubN)parent).parent)).data.append("pF");
			}
			@Override
			protected void unpauseAction() {
				((Active)(((SubN)parent).parent)).data.append("uF");
			}
	
			}
	}

}
