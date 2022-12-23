package fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Test;

import fsm.demo.Event;
import fsm.demo.VL_FSM;

public class VL_FSMTest {
	
	@Test
	public void simpleStateChange() {
		VL_FSM fsm = new VL_FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.A.class));
		fsm.handleEvent(Event.bypass);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.B.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void defaultEntry() {
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
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
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
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
	}

	@Test
	public void explicitOuterExit() {
		VL_FSM fsm = new VL_FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Normal.class));
		fsm.handleEvent(Event.error);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.E.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void explicitInnerExit() {
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
		fsm.handleEvent(Event.last);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.C.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void defaultExit() {
		VL_FSM fsm = new VL_FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.High.class));
		fsm.handleEvent(Event.exceed);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.D.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void enterShallowHistoryAfterExplicitInnerExit() {
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
		fsm.handleEvent(Event.last);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.C.class));

		fsm.handleEvent(Event.toF);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.F.class));
		fsm.handleEvent(Event.clear);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Low.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
	}

	@Test
	public void enterShallowHistoryAfterExplicitOuterExit() {
		VL_FSM fsm = new VL_FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.High.class));
		fsm.handleEvent(Event.error);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.E.class));

		fsm.handleEvent(Event.toF);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.F.class));
		fsm.handleEvent(Event.clear);
		assertThat(fsm.getSubstates().get(0), instanceOf(VL_FSM.Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(VL_FSM.Sub.High.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
	}

	@Test
	public void contextData() {
		VL_FSM fsm = new VL_FSM();
		assertEquals("iA", fsm.data.toString());
	}
}
