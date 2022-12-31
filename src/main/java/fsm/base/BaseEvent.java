package fsm.base;

public class BaseEvent<E extends Enum<E>, P> {
	E event;
	P payload;

	public BaseEvent(E e, P p) {
		event = e;
		payload = p;
	}
}
