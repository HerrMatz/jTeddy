package fsm;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Test;

import fsm.demo.Event;
import fsm.demo.VL_FSM;

public class VL_FSMTest {
	
	@Test
	public void simple() {
		VL_FSM fsm = new VL_FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.High.class));
		fsm.handleEvent(Event.down);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Low.class));
		fsm.handleEvent(Event.clear);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Normal.class));
		fsm.handleEvent(Event.tick);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Normal.class));
	}

	@Test
	public void explicitEntry() {
		VL_FSM fsm = new VL_FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.A.class));
		fsm.handleEvent(Event.bypass);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.B.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.High.class));
	}

	@Test
	public void defaultExit() {
		VL_FSM fsm = new VL_FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Normal.class));
		fsm.handleEvent(Event.last);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.C.class));
	}
}
