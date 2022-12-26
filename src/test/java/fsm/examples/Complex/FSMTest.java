package fsm.examples.Complex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

public class FSMTest {

	@Test
	public void orthogonalDefaultEntry() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Init.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.A.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.C.class));
	}

}
