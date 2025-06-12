package com.scratch.model;

import com.scratch.enumeration.ImpactEnum;

import java.util.List;
import java.util.Map;

public class GameConfig {
    public int columns = 3;
    public int rows = 3;
    public Map<String, Symbol> symbols;
    public Probabilities probabilities;
    public Map<String, WinCombination> win_combinations;

    public static class Symbol {
        public double reward_multiplier;
        public String type;
        public Double extra;
        public ImpactEnum impact;
    }

    public static class Probabilities {
        public List<CellProbability> standard_symbols;
        public BonusSymbolProbabilities bonus_symbols;
    }

    public static class CellProbability {
        public int column;
        public int row;
        public Map<String, Integer> symbols;
    }

    public static class BonusSymbolProbabilities {
        public Map<String, Integer> symbols;
    }

    public static class WinCombination {
        public double reward_multiplier;
        public String when;
        public Integer count;
        public String group;
        public List<List<String>> covered_areas;
    }
}
