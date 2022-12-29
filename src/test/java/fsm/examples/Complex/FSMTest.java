package fsm.examples.Complex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

public class FSMTest {

	@Test
	public void parallelDefaultEntry() {
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

	@Test
	public void parallelDefaultExit() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Init.class));
		fsm.handleEvent(Event.start);
		fsm.handleEvent(Event.advance);
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.B1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.C.class));
		fsm.handleEvent(Event.advance);
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.B2.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.C.class));
		fsm.handleEvent(Event.advance);
		assertEquals(1, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub2.C.class));
		fsm.handleEvent(Event.advance);
		assertEquals(1, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub2.D.class));
		fsm.handleEvent(Event.advance);
		assertEquals(1, fsm.getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Inactive.class));
		assertEquals(0, fsm.getSubstate(0).getSubstates().size());
	}

	@Test
	public void parallelExplicitOuterExitDeepHistory() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Init.class));
		fsm.handleEvent(Event.start);
		fsm.handleEvent(Event.advance);
		fsm.handleEvent(Event.advance);
		fsm.handleEvent(Event.toD);
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.B2.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.D.class));

		fsm.handleEvent(Event.exit);
		fsm.handleEvent(Event.advance);	// will be ignored
		assertEquals(1, fsm.getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Inactive.class));
		assertEquals(0, fsm.getSubstate(0).getSubstates().size());

		fsm.handleEvent(Event.deep);
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.B2.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.D.class));
	}

	@Test
	public void parallelExplicitOuterExitShallowHistory() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Init.class));
		fsm.handleEvent(Event.start);
		fsm.handleEvent(Event.advance);
		fsm.handleEvent(Event.advance);
		fsm.handleEvent(Event.toD);
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.B.B2.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.D.class));

		fsm.handleEvent(Event.exit);
		fsm.handleEvent(Event.advance);	// will be ignored
		assertEquals(1, fsm.getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Inactive.class));
		assertEquals(0, fsm.getSubstate(0).getSubstates().size());

		fsm.handleEvent(Event.shallow);
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.A.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.C.class));
	}

	@Test
	public void parallelAdd() {
		FSM fsm = new FSM();
		fsm.handleEvent(Event.start);
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(2, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(Active.class));
		assertThat(fsm.getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.class));
		assertThat(fsm.getSubstate(0).getSubstate(1), instanceOf(Active.Sub2.class));
		assertThat(fsm.getSubstate(0).getSubstate(0).getSubstate(0), instanceOf(Active.Sub1.A.class));
		assertThat(fsm.getSubstate(0).getSubstate(1).getSubstate(0), instanceOf(Active.Sub2.C.class));
		fsm.handleEvent(Event.inner);
		assertEquals(3, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(2), instanceOf(Active.SubN.class));
		assertEquals(1, fsm.getSubstate(0).getSubstate(2).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(2).getSubstate(0), instanceOf(Active.SubN.E.class));
		assertEquals("iSiE", ((Active)fsm.getSubstate(0)).data.toString());
		fsm.handleEvent(Event.inner);
		assertEquals(4, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(2), instanceOf(Active.SubN.class));
		// assertEquals(1, fsm.getSubstate(0).getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(2).getSubstate(0), instanceOf(Active.SubN.E.class));
		assertThat(fsm.getSubstate(0).getSubstate(3), instanceOf(Active.SubN.class));
		// assertEquals(1, fsm.getSubstate(0).getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(3).getSubstate(0), instanceOf(Active.SubN.E.class));
		assertEquals("iSiEiSiE", ((Active)fsm.getSubstate(0)).data.toString());
		fsm.handleEvent(Event.toF);
		assertEquals(4, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(2), instanceOf(Active.SubN.class));
		assertThat(fsm.getSubstate(0).getSubstate(2).getSubstate(0), instanceOf(Active.SubN.F.class));
		assertThat(fsm.getSubstate(0).getSubstate(3), instanceOf(Active.SubN.class));
		assertThat(fsm.getSubstate(0).getSubstate(3).getSubstate(0), instanceOf(Active.SubN.E.class));
		assertEquals("iSiEiSiEoEiF", ((Active)fsm.getSubstate(0)).data.toString());

		fsm.handleEvent(Event.exit);
		fsm.handleEvent(Event.advance);	// will be ignored
		assertEquals(1, fsm.getSubstates().size());
		assertThat(fsm.getSubstate(0), instanceOf(FSM.Inactive.class));
		assertEquals(0, fsm.getSubstate(0).getSubstates().size());
		// assertEquals("iSiEiSiEoEiFpEpSpFpS", ((Active)fsm.getSubstate(0)).data.toString());

		fsm.handleEvent(Event.deep);
		assertEquals(4, fsm.getSubstate(0).getSubstates().size());
		assertThat(fsm.getSubstate(0).getSubstate(2), instanceOf(Active.SubN.class));
		assertThat(fsm.getSubstate(0).getSubstate(2).getSubstate(0), instanceOf(Active.SubN.F.class));
		assertThat(fsm.getSubstate(0).getSubstate(3), instanceOf(Active.SubN.class));
		assertThat(fsm.getSubstate(0).getSubstate(3).getSubstate(0), instanceOf(Active.SubN.E.class));
		assertEquals("iSiEiSiEoEiFpFpEpSpSuSuSuFuE", ((Active)fsm.getSubstate(0)).data.toString());
	}

}
