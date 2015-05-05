package austinmckinley.pitboss.status;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import austinmckinley.pitboss.status.dataobjects.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import austinmckinley.pitboss.status.dataobjects.PlayerTurnStatus;
import austinmckinley.pitboss.status.dataobjects.StatusRecord;

public class StatusParser {
	private BufferedReader inputLogReader;
	private StatusRecord statusRecord;
	
	private static Pattern setTurnActivePattern = Pattern.compile("\\[[0-9]*\\.[0-9]*\\] DBG: changeNumGameTurnActive\\([0-9]\\) m_iNumActive=[0-9]* : setTurnActive\\(\\) for player ([0-9]*) (.*)$");
	private static Pattern netTurnCompletePattern = Pattern.compile("\\[[0-9]*\\.[0-9]*\\] Net SEND \\(.*\\): .*: NetTurnComplete : Turn Complete, ([0-9]+), .*$");
	private static Pattern netTurnUnreadyPattern = Pattern.compile("\\[[0-9]*\\.[0-9]*\\] Net RECV \\(.*\\) :NetTurnUnready : Turn Complete, ([0-9]+) .*$");
	private static Pattern netChatPattern = Pattern.compile("\\[[0-9]*\\.[0-9]*\\] Net RECV \\(.*\\) :NetChat : Player ([0-9]+) said \"(.*)\"$");
	private static Pattern gameTurnPattern = Pattern.compile("\\[[0-9]*\\.[0-9]*\\] DBG: Game Turn ([0-9]+)$");

	@Autowired
	public StatusParser(BufferedReader inputLogReader) {
		this.inputLogReader = inputLogReader;
		this.statusRecord = new StatusRecord();
	}
	
	public void generateInitialStatusRecord() throws IOException {
		while(inputLogReader.ready()) {
			String lineText = inputLogReader.readLine();
			if(lineText == null) {
				break;
			}
			
			processSingleLine(lineText);		
		}
	}
	
	private void processSingleLine(String lineText) {
		Matcher setTurnActiveMatcher = setTurnActivePattern.matcher(lineText);
		if (setTurnActiveMatcher.matches()) {

			String playerId = setTurnActiveMatcher.group(1);
			String playerName = setTurnActiveMatcher.group(2);

			PlayerTurnStatus playerStatus = new PlayerTurnStatus(playerName, false);
			statusRecord.getPlayerStatuses().put(playerId, playerStatus);
			
			return;
		}

		Matcher netTurnCompleteMatcher = netTurnCompletePattern.matcher(lineText);
		if (netTurnCompleteMatcher.matches()) {
			String playerId = netTurnCompleteMatcher.group(1);

			statusRecord.getPlayerStatuses().get(playerId).setTurnFinished(true);
			return;
		}

		Matcher netTurnUnreadyMatcher = netTurnUnreadyPattern.matcher(lineText);
		if (netTurnUnreadyMatcher.matches()) {
			String playerId = netTurnUnreadyMatcher.group(1);

			statusRecord.getPlayerStatuses().get(playerId).setTurnFinished(false);
			return;
		}

		Matcher netChatMatcher = netChatPattern.matcher(lineText);
		if (netChatMatcher.matches()) {
			String playerId = netChatMatcher.group(1);
			String messageText = netChatMatcher.group(2);

			statusRecord.getChatMessages().add(new ChatMessage(playerId, messageText));
			return;
		}

		Matcher gameTurnMatcher = gameTurnPattern.matcher(lineText);
		if (gameTurnMatcher.matches()) {
			int turnNumber = Integer.parseInt(gameTurnMatcher.group(1));
			statusRecord.setTurnNumber(turnNumber);

			return;
		}
	}

	public StatusRecord checkAndUpdateCurrentStatus() {
		if (updatesExist()) {
		}
		throw new NotImplementedException();
	}
	
	public StatusRecord getCurrentStatus() {
		return statusRecord;
	}

	private boolean updatesExist() {
		return false;
	}
}
