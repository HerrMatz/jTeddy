package fsm.examples.Complex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

public class FSMTest {
	@Test
	public void defaultEntryOrthogonal() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.Init.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(Active.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstates().get(0).getSubstates().size());
	}

}
