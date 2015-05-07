package austinmckinley.pitboss.status;

import austinmckinley.pitboss.status.dataobjects.PlayerTurnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/status")
public class StatusController {

    @Autowired StatusParser statusParser;

	@RequestMapping(value="/test", method = RequestMethod.GET)
	public String getIndex(Model model) {
        List<PlayerTurnStatus> playerTurnStatuses = statusParser
                .getCurrentStatus()
                .getPlayerStatuses()
                .entrySet()
                .stream()
                .map(entry -> entry.getValue())
                .collect(toList());
		model.addAttribute("playerTurnStatuses", playerTurnStatuses);
		return "status-index";
	}
}