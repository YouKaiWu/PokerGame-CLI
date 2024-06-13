import java.util.*;
import java.util.stream.Collectors;

public class HandEvaluator {
    private static final Map<String, Integer> RANK_VALUES = createRankValues();

    private static Map<String, Integer> createRankValues() {
        Map<String, Integer> rankValues = new HashMap<>();
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (int i = 0; i < ranks.length; i++) {
            rankValues.put(ranks[i], i + 2);
        }
        return rankValues;
    }

    public static int evaluateHand(List<Card> hand) {
        List<List<Card>> combinations = generateCombinations(hand, 5);
        int bestScore = 0;
        for (List<Card> combination : combinations) {
            int score = evaluateCombination(combination);
            if (score > bestScore) {
                bestScore = score;
            }
        }
        return bestScore;
    }

    public static String getHandType(List<Card> hand) {
        List<List<Card>> combinations = generateCombinations(hand, 5);
        int bestScore = 0;
        String bestHandType = "High Card";
        for (List<Card> combination : combinations) {
            int score = evaluateCombination(combination);
            String handType = determineHandType(combination);
            if (score > bestScore) {
                bestScore = score;
                bestHandType = handType;
            }
        }
        return bestHandType;
    }

    private static String determineHandType(List<Card> combination) {
        combination.sort(Comparator.comparingInt(c -> RANK_VALUES.get(c.getRank())));

        if (isRoyalFlush(combination)) return "Royal Flush";
        if (isStraightFlush(combination)) return "Straight Flush";
        if (isFourOfAKind(combination)) return "Four of a Kind";
        if (isFullHouse(combination)) return "Full House";
        if (isFlush(combination)) return "Flush";
        if (isStraight(combination)) return "Straight";
        if (isThreeOfAKind(combination)) return "Three of a Kind";
        if (isTwoPair(combination)) return "Two Pair";
        if (isOnePair(combination)) return "One Pair";
        
        return "High Card";
    }

    private static int evaluateCombination(List<Card> combination) {
        combination.sort(Comparator.comparingInt(c -> RANK_VALUES.get(c.getRank())));

        if (isRoyalFlush(combination)) return 9000000;
        if (isStraightFlush(combination)) return 8000000 + getHighCardValue(combination);
        if (isFourOfAKind(combination)) return 7000000 + getFourOfAKindValue(combination);
        if (isFullHouse(combination)) return 6000000 + getFullHouseValue(combination);
        if (isFlush(combination)) return 5000000 + getHighCardValues(combination);
        if (isStraight(combination)) return 4000000 + getHighCardValue(combination);
        if (isThreeOfAKind(combination)) return 3000000 + getThreeOfAKindValue(combination);
        if (isTwoPair(combination)) return 2000000 + getTwoPairValue(combination);
        if (isOnePair(combination)) return 1000000 + getOnePairValue(combination);
        
        return getHighCardValues(combination);
    }

    private static boolean isRoyalFlush(List<Card> hand) {
        return isStraightFlush(hand) && hand.get(4).getRank().equals("A");
    }

    private static boolean isStraightFlush(List<Card> hand) {
        return isFlush(hand) && isStraight(hand);
    }

    private static boolean isFourOfAKind(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(4L);
    }

    private static boolean isFullHouse(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(3L) && rankCounts.containsValue(2L);
    }

    private static boolean isFlush(List<Card> hand) {
        String suit = hand.get(0).getSuit();
        for (Card card : hand) {
            if (!card.getSuit().equals(suit)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStraight(List<Card> hand) {
        int[] ranks = hand.stream().mapToInt(c -> RANK_VALUES.get(c.getRank())).toArray();
        for (int i = 0; i < ranks.length - 1; i++) {
            if (ranks[i] + 1 != ranks[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isThreeOfAKind(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(3L);
    }

    private static boolean isTwoPair(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        return rankCounts.values().stream().filter(count -> count == 2).count() == 2;
    }

    private static boolean isOnePair(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(2L);
    }

    private static Map<String, Long> getRankCounts(List<Card> hand) {
        Map<String, Long> rankCounts = new HashMap<>();
        for (Card card : hand) {
            rankCounts.put(card.getRank(), rankCounts.getOrDefault(card.getRank(), 0L) + 1);
        }
        return rankCounts;
    }

    private static List<List<Card>> generateCombinations(List<Card> cards, int k) {
        List<List<Card>> combinations = new ArrayList<>();
        generateCombinationsRecursive(combinations, new ArrayList<>(), cards, k, 0);
        return combinations;
    }

    private static void generateCombinationsRecursive(List<List<Card>> combinations, List<Card> tempCombination, List<Card> cards, int k, int start) {
        if (tempCombination.size() == k) {
            combinations.add(new ArrayList<>(tempCombination));
            return;
        }

        for (int i = start; i < cards.size(); i++) {
            tempCombination.add(cards.get(i));
            generateCombinationsRecursive(combinations, tempCombination, cards, k, i + 1);
            tempCombination.remove(tempCombination.size() - 1);
        }
    }

    private static int getHighCardValue(List<Card> hand) {
        return RANK_VALUES.get(hand.get(4).getRank());
    }

    private static int getHighCardValues(List<Card> hand) {
        int value = 0;
        for (int i = 4; i >= 0; i--) {
            value = value * 14 + RANK_VALUES.get(hand.get(i).getRank());
        }
        return value;
    }

    private static int getFourOfAKindValue(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        String fourRank = rankCounts.entrySet().stream().filter(e -> e.getValue() == 4).map(Map.Entry::getKey).findFirst().orElse("");
        int fourValue = RANK_VALUES.get(fourRank);
        int kickerValue = hand.stream().filter(c -> !c.getRank().equals(fourRank)).mapToInt(c -> RANK_VALUES.get(c.getRank())).max().orElse(0);
        return fourValue * 14 + kickerValue;
    }

    private static int getFullHouseValue(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        String threeRank = rankCounts.entrySet().stream().filter(e -> e.getValue() == 3).map(Map.Entry::getKey).findFirst().orElse("");
        String pairRank = rankCounts.entrySet().stream().filter(e -> e.getValue() == 2).map(Map.Entry::getKey).findFirst().orElse("");
        int threeValue = RANK_VALUES.get(threeRank);
        int pairValue = RANK_VALUES.get(pairRank);
        return threeValue * 14 + pairValue;
    }

    private static int getThreeOfAKindValue(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        String threeRank = rankCounts.entrySet().stream().filter(e -> e.getValue() == 3).map(Map.Entry::getKey).findFirst().orElse("");
        int threeValue = RANK_VALUES.get(threeRank);
        List<Integer> kickers = hand.stream().filter(c -> !c.getRank().equals(threeRank)).map(c -> RANK_VALUES.get(c.getRank())).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        return threeValue * 14 * 14 + kickers.get(0) * 14 + kickers.get(1);
    }

    private static int getTwoPairValue(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        List<String> pairs = rankCounts.entrySet().stream().filter(e -> e.getValue() == 2).map(Map.Entry::getKey).sorted(Comparator.comparingInt(RANK_VALUES::get).reversed()).collect(Collectors.toList());
        int highPairValue = RANK_VALUES.get(pairs.get(0));
        int lowPairValue = RANK_VALUES.get(pairs.get(1));
        int kickerValue = hand.stream().filter(c -> !pairs.contains(c.getRank())).mapToInt(c -> RANK_VALUES.get(c.getRank())).max().orElse(0);
        return highPairValue * 14 * 14 + lowPairValue * 14 + kickerValue;
    }

    private static int getOnePairValue(List<Card> hand) {
        Map<String, Long> rankCounts = getRankCounts(hand);
        String pairRank = rankCounts.entrySet().stream().filter(e -> e.getValue() == 2).map(Map.Entry::getKey).findFirst().orElse("");
        int pairValue = RANK_VALUES.get(pairRank);
        List<Integer> kickers = hand.stream().filter(c -> !c.getRank().equals(pairRank)).map(c -> RANK_VALUES.get(c.getRank())).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        return pairValue * 14 * 14 * 14 + kickers.get(0) * 14 * 14 + kickers.get(1) * 14 + kickers.get(2);
    }
}