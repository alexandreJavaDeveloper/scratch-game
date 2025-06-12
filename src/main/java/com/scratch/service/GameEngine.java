package com.scratch.service;

import com.scratch.enumeration.TypeEnum;
import com.scratch.model.GameConfig;
import com.scratch.model.GameResult;

import java.util.*;

public class GameEngine {

    private final GameConfig config;
    private final Random random = new Random();

    public GameEngine(GameConfig config) {
        this.config = config;
    }

    public GameResult playGame(final int betAmount) {
        final String[][] matrix = generateMatrix();
        final Map<String, List<String>> winMap = evaluateWinningCombinations(matrix);
        final boolean hasWin = !winMap.isEmpty();
        String bonusSymbol = null;
        double reward = calculateReward(winMap, betAmount);

        if (hasWin) {
            bonusSymbol = rollBonusSymbol();
            reward = applyBonus(reward, bonusSymbol);
        }

        return new GameResult(matrix, reward, winMap, bonusSymbol);
    }

    private String[][] generateMatrix() {
        final String[][] matrix = new String[config.rows][config.columns];

        for (int r = 0; r < config.rows; r++) {
            for (int c = 0; c < config.columns; c++) {
                final GameConfig.CellProbability cellProb = getCellProbability(r, c);
                matrix[r][c] = rollSymbol(cellProb.symbols);
            }
        }

        return matrix;
    }

    private GameConfig.CellProbability getCellProbability(final int row, final int col) {
        for (GameConfig.CellProbability cp : config.probabilities.standard_symbols) {
            if (cp.row == row && cp.column == col) return cp;
        }
        return config.probabilities.standard_symbols.get(0);
    }

    private String rollSymbol(final Map<String, Integer> symbolProbabilities) {
        final int total = symbolProbabilities.values().stream().mapToInt(i -> i).sum();
        final int roll = random.nextInt(total);
        int cumulative = 0;

        for (Map.Entry<String, Integer> entry : symbolProbabilities.entrySet()) {
            cumulative += entry.getValue();

            if (roll < cumulative) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Error while rolling symbol.");
    }

    private Map<String, List<String>> evaluateWinningCombinations(final String[][] matrix) {
        final Map<String, List<String>> result = new HashMap<>();
        final Map<String, Integer> countMap = new HashMap<>();

        for (String[] row : matrix) {
            for (String symbol : row) {
                if (!isStandardSymbol(symbol)) continue;
                countMap.put(symbol, countMap.getOrDefault(symbol, 0) + 1);
            }
        }

        for (Map.Entry<String, GameConfig.WinCombination> entry : config.win_combinations.entrySet()) {
            final String winName = entry.getKey();
            final GameConfig.WinCombination combo = entry.getValue();

            switch (combo.when) {
                case "same_symbols":
                    for (Map.Entry<String, Integer> e : countMap.entrySet()) {
                        if (e.getValue() >= combo.count) {
                            result.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(winName);
                        }
                    }
                    break;
                case "linear_symbols":
                    for (List<String> area : combo.covered_areas) {
                        final String firstSymbol = getSymbolAt(matrix, area.get(0));
                        if (!isStandardSymbol(firstSymbol)) {
                            continue;
                        }
                        if (area.stream().allMatch(pos -> firstSymbol.equals(getSymbolAt(matrix, pos)))) {
                            result.computeIfAbsent(firstSymbol, k -> new ArrayList<>()).add(winName);
                        }
                    }
                    break;
            }
        }

        return result;
    }

    private String getSymbolAt(final String[][] matrix, final String position) {
        final String[] parts = position.split(":");
        final int row = Integer.parseInt(parts[0]);
        final int col = Integer.parseInt(parts[1]);
        return matrix[row][col];
    }

    private boolean isStandardSymbol(String symbol) {
        return config.symbols.containsKey(symbol) && TypeEnum.standard.getValue().equals(config.symbols.get(symbol).type);
    }

    private double calculateReward(final Map<String, List<String>> winMap, final int betAmount) {
        double totalReward = 0;

        for (Map.Entry<String, List<String>> entry : winMap.entrySet()) {
            final String symbol = entry.getKey();
            double baseReward = betAmount * config.symbols.get(symbol).reward_multiplier;

            for (String combo : entry.getValue()) {
                baseReward *= config.win_combinations.get(combo).reward_multiplier;
            }

            totalReward += baseReward;
        }

        return totalReward;
    }

    private String rollBonusSymbol() {
        final Map<String, Integer> bonusMap = config.probabilities.bonus_symbols.symbols;
        return rollSymbol(bonusMap);
    }

    private double applyBonus(double reward, String bonusSymbol) {
        final GameConfig.Symbol bonus = config.symbols.get(bonusSymbol);
        if (bonus == null || reward <= 0) {
            return reward;
        }

        return switch (bonus.impact) {
            case multiply_reward -> reward * bonus.reward_multiplier;
            case extra_bonus -> reward + bonus.extra;
            case miss -> reward;
        };
    }
}