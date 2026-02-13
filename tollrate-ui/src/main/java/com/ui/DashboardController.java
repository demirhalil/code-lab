package com.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
@RequiredArgsConstructor
public class DashboardController {

	private final WebClient.Builder loadBalancedWebClientBuilder;

    @RequestMapping("/dashboard")
	public String GetTollRate(@RequestParam(defaultValue = "1000") Integer stationId, Model m) {

		WebClient client = loadBalancedWebClientBuilder.build();

        TollRate rate = client.get()
            .uri("http://tollrate-service/tollrate/" + stationId)
            .retrieve()
            .bodyToMono(TollRate.class)
            .block();
		
		System.out.println("stationId: " + stationId);
		m.addAttribute("rate", rate);
		return "dashboard";
	}
    
}
