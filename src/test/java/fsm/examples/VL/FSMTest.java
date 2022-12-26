package fsm.examples.VL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Test;

import fsm.examples.VL.Event;
import fsm.examples.VL.Sub;
import fsm.examples.VL.FSM;

public class FSMTest {
	
	@Test
	public void simpleStateChange() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.bypass);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.B.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void defaultEntry() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		fsm.handleEvent(Event.down);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Low.class));
		fsm.handleEvent(Event.clear);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		fsm.handleEvent(Event.tick);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
	}

	@Test
	public void explicitEntry() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.bypass);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.B.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
	}

	@Test
	public void explicitOuterExit() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		fsm.handleEvent(Event.error);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.E.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void explicitInnerExit() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		fsm.handleEvent(Event.down);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Low.class));
		fsm.handleEvent(Event.last);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.C.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void defaultExit() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		fsm.handleEvent(Event.exceed);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.D.class));
		assertEquals(1, fsm.getSubstates().size());
	}

	@Test
	public void enterShallowHistoryAfterExplicitInnerExit() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		fsm.handleEvent(Event.down);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Low.class));
		fsm.handleEvent(Event.last);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.C.class));

		fsm.handleEvent(Event.toF);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.F.class));
		fsm.handleEvent(Event.clear);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Low.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
	}

	@Test
	public void enterShallowHistoryAfterExplicitOuterExit() {
		FSM fsm = new FSM();
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.A.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Normal.class));
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		fsm.handleEvent(Event.error);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.E.class));

		fsm.handleEvent(Event.toF);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.F.class));
		fsm.handleEvent(Event.clear);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		assertEquals(1, fsm.getSubstates().size());
		assertEquals(1, fsm.getSubstates().get(0).getSubstates().size());
	}

	@Test
	public void contextData() {
		FSM fsm = new FSM();
		assertEquals("iA", fsm.data.toString());
	}
	
	@Test
	public void entryExitActionOrderDefaultExit() {
		FSM fsm = new FSM();
		assertEquals("iA", fsm.data.toString());
		fsm.handleEvent(Event.start);
		assertEquals("iAoAiSiN", fsm.data.toString());
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		assertEquals("iAoAiSiNoNiH", fsm.data.toString());
		fsm.handleEvent(Event.exceed);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.D.class));
		assertEquals("iAoAiSiNoNiHoHoSiD", fsm.data.toString());
	}
	
	@Test
	public void entryExitActionOrderExplicitInnerExit() {
		FSM fsm = new FSM();
		assertEquals("iA", fsm.data.toString());
		fsm.handleEvent(Event.start);
		assertEquals("iAoAiSiN", fsm.data.toString());
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		assertEquals("iAoAiSiNoNiH", fsm.data.toString());
		fsm.handleEvent(Event.down);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.Low.class));
		assertEquals("iAoAiSiNoNiHoHiL", fsm.data.toString());
		fsm.handleEvent(Event.last);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.C.class));
		assertEquals("iAoAiSiNoNiHoHiLpLpSiC", fsm.data.toString());
	}
	
	@Test
	public void entryExitActionOrderExplicitOuterExit() {
		FSM fsm = new FSM();
		assertEquals("iA", fsm.data.toString());
		fsm.handleEvent(Event.start);
		assertEquals("iAoAiSiN", fsm.data.toString());
		fsm.handleEvent(Event.error);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.E.class));
		assertEquals("iAoAiSiNpNpSiE", fsm.data.toString());
	}
	
	@Test
	public void entryExitActionOrderDeepHistory() {
		FSM fsm = new FSM();
		assertEquals("iA", fsm.data.toString());
		fsm.handleEvent(Event.start);
		assertEquals("iAoAiSiN", fsm.data.toString());
		fsm.handleEvent(Event.up);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		assertEquals("iAoAiSiNoNiH", fsm.data.toString());
		fsm.handleEvent(Event.error);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.E.class));
		assertEquals("iAoAiSiNoNiHpHpSiE", fsm.data.toString());
		fsm.handleEvent(Event.toF);
		assertThat(fsm.getSubstates().get(0), instanceOf(FSM.F.class));
		assertEquals("iAoAiSiNoNiHpHpSiEoEiF", fsm.data.toString());
		fsm.handleEvent(Event.clear);
		assertThat(fsm.getSubstates().get(0), instanceOf(Sub.class));
		assertThat(fsm.getSubstates().get(0).getSubstates().get(0), instanceOf(Sub.High.class));
		assertEquals("iAoAiSiNoNiHpHpSiEoEiFoFuSuH", fsm.data.toString());
	}
	
}
