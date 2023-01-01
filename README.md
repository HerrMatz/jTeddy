# What is jTeddy?
jTeddy is a Java framework that allows for quick and concise implementation of hierarchical and parallel finite state machines.

# Features
* Follows the GOF state pattern
* States can be implemented as nested classes to improve readability
* Supports hierarchical state machines
* Supports parallel (orthogonal) state machines
* Supports shallow and deep history nodes
* No pseudo states necessary
* Comprehensive [examples](src/main/java/fsm/examples)
* Minimal implementation overhead
* Easy to debug
* Easy to test
* Entry and exit actions are called automatically
* Input alphabet declared by enum
* Generic arguments can be passed to transitions
* Shared data:
  * All states share the same generic context data
* Individual data:
  * States may contain their own fields
  * Substates may access their parent's fields
  * Individual data is restored on entry by history node
* Distinction between regular entry and entry by history node (unpause):
  * Seperate entry action (for regular entry) and unpause action (for entry by history node) methods can be defined
* Distinction between regular exit and explicit exit (pause):
  * Seperate exit action (for regular exit) and pause action (for explicit exit) methods can be defined
  * Useful if the superstate is not left on behalf of its substate(s) but from outside influences (e.g. an emergency stop which may not be wanted to trigger the substates' exit actions).
  * Not defined in the UML standard
* New substates of a given type can be added dynamically at runtime
  * Not defined in the UML standard

# Examples
A simple state called Inactive that has four simple transitions:

	public static class Inactive extends MyState {
		public Inactive(MyState from) {
			super(from);
			TRANSITION(Event.start, (e -> ENTER(new Active(this))));
			TRANSITION(Event.deep, (e -> ENTER_DEEP(new Active(this))));
			TRANSITION(Event.shallow, (e -> ENTER_SHALLOW(new Active(this))));
			TRANSITION(Event.exit, (e -> EXIT()));
		}
	}

---

A simple state with a more complex transition that makes use of transition parameters:

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

Note that the `entryAction()` and `exitAction()` are called automatically during a state change. Both methods as well as the transition access the `contextData` which is shared by all states of a state machine object.

---

States may contain their own fields that can be accessed by themself and their immediate substates by cast. By use of the `get()` method, even indirect substates (such as subsubstates) may access another states data. This is hacky, not recommended and indicates a bad state machine design.

	public class Active extends MyState {

	public StringBuilder data = new StringBuilder();

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
		}
	}

---

To declare the input alphabet, the transition payload type as well as the type of the shared context data, you need to override the base state class with a base class of your own:

	public abstract class MyState extends State<Event, Integer, SimpleContextData> {
		public MyState(MyState other) {
			super(other, Event.class);
		}
	}

where Event is an enumeration that defines all the events you want to send, Integer is the payload type and SimpleContextData is the shared context data type. All your states will be derived from your new base state class.

---

For more and more comprehensive examples see [here](src/main/java/fsm/examples). The respective *_HowTo.svg files depict the necessary method calls for changing states.

---

Questions, bugs, rebuke, improvement ideas? Feel free to contact me on [LinkedIn](https://www.linkedin.com/in/matz-heitm%C3%BCller-161a84205/) :)

