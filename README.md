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

# Examples
A simple state called Inactive that has four transitions:

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

To declare the input alphabet, the transition payload type as well as the type of the shared context data, you need to override the base state class with a base class of your own:

	public abstract class MyState extends State<Event, Integer, SimpleContextData> {
		public MyState(MyState other) {
			super(other, Event.class);
		}
		// for explicit entries
		public MyState(MyState from, MyState entry) {
			super(from, entry, Event.class);
		}
	}

where Event is an enumeration that defines all the events you want to send, Integer is the payload type and SimpleContextData is the shared context data type. All your states will be derived from your new base state class.

---

For more and more comprehensive examples see [here](src/main/java/fsm/examples).