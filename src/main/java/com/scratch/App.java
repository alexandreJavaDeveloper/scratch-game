package com.scratch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratch.model.GameConfig;
import com.scratch.model.GameResult;
import com.scratch.service.GameEngine;
import java.io.File;

public class App {

    public static void main(String[] args) throws Exception {
        if (args.length < 4 || !args[0].equals("--config") || !args[2].equals("--betting-amount")) {
            System.out.println("Usage: java -jar scratchGame-1.0.jar --config <config.json> --betting-amount <amount>");
            return;
        }

        final String configPath = args[1];
        final int betAmount = Integer.parseInt(args[3]);

        final ObjectMapper mapper = new ObjectMapper();
        final GameConfig config = mapper.readValue(new File(configPath), GameConfig.class);

        final GameEngine engine = new GameEngine(config);
        final GameResult result = engine.playGame(betAmount);

        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }
}