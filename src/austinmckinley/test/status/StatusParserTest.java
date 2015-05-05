package austinmckinley.test.status;

import austinmckinley.pitboss.status.StatusParser;
import austinmckinley.pitboss.status.dataobjects.ChatMessage;
import austinmckinley.pitboss.status.dataobjects.PlayerTurnStatus;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class StatusParserTest {

	@Test
	public void testGenerateInitialStatusRecord() throws Exception {
		String logInput = "[3901872.406] Net SEND (8): size=36: NetAICivsProcessed, 8, Game Turn 69, 0/1\n"
				+ "[3901872.406] DBG: changeNumGameTurnActive(1) m_iNumActive=1 : setTurnActive() for player 0 Prometheus\n"
				+ "[3901872.437] DBG: changeNumGameTurnActive(1) m_iNumActive=2 : setTurnActive() for player 1 Muzzy\n"
				+ "[3901753.140] Net SEND (1, 8): size=32: NetTurnComplete : Turn Complete, 1, 1/2\n"
				+ "[4005074.218] Net RECV (1) :NetChat : Player 1 said \"hello world\"\n";
		StatusParser gateway = createGatewayFromInputString(logInput);

	
		gateway.generateInitialStatusRecord();


		assertThat(gateway.getCurrentStatus().getPlayerStatuses().get("0"), equalTo(new PlayerTurnStatus("Prometheus", false)));
		assertThat(gateway.getCurrentStatus().getPlayerStatuses().get("1"), equalTo(new PlayerTurnStatus("Muzzy", true)));
		assertEquals(gateway.getCurrentStatus().getChatMessages(), asList(new ChatMessage("1", "hello world")
		));
	}

	private StatusParser createGatewayFromInputString(String logInput) {
		BufferedReader inputLogReader = new BufferedReader(new StringReader(logInput));
		return new StatusParser(inputLogReader);
	}
}
