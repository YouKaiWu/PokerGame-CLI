import java.util.*;

public class PokerGame {
    private Deck deck;
    private List<Player> players;
    private List<Card> communityCards;
    private int smallBlind;
    private int bigBlind;
    private int dealerPosition;
    private int smallBlindPosition;
    private int bigBlindPosition;
    private Scanner scanner;
    private int currentBet;
    private List<Pot> pots;

    public PokerGame(List<Player> players, int smallBlind, int bigBlind) {
        this.players = players;
        this.deck = new Deck();
        this.communityCards = new ArrayList<>();
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.scanner = new Scanner(System.in);
        this.currentBet = bigBlind;
        this.pots = new ArrayList<>(); //
        determineDealer();
        setBlinds();
    }

    private void determineDealer() {
        dealerPosition = 0; // For simplicity, let's just start with the first player as the dealer
    }

    private void setBlinds() {
        smallBlindPosition = (dealerPosition + 1) % players.size();
        bigBlindPosition = (dealerPosition + 2) % players.size();
        players.get(smallBlindPosition).placeBet(smallBlind);
        players.get(bigBlindPosition).placeBet(bigBlind);
    }

    public void startGame() {
        dealStartingHands();
        performBettingRound(bigBlindPosition + 1, "Pre-flop");
        if (playersStillInGame() > 1) {
            dealFlop();
            performBettingRound(smallBlindPosition, "Flop");
        }
        if (playersStillInGame() > 1) {
            dealTurn();
            performBettingRound(smallBlindPosition, "Turn");
        }
        if (playersStillInGame() > 1) {
            dealRiver();
            performBettingRound(smallBlindPosition, "River");
        }
        if (playersStillInGame() > 1) {
            createPots();
            distributePots();
        }
    }

    private void dealStartingHands() {
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                player.receiveCard(deck.dealCard());
            }
        }
    }

    private void dealFlop() {
        deck.dealCard(); // Burn a card
        for (int i = 0; i < 3; i++) {
            communityCards.add(deck.dealCard());
        }
        System.out.println("Community cards: " + communityCards);
    }

    private void dealTurn() {
        deck.dealCard(); // Burn a card
        communityCards.add(deck.dealCard());
        System.out.println("Community cards: " + communityCards);
    }

    private void dealRiver() {
        deck.dealCard(); // Burn a card
        communityCards.add(deck.dealCard());
        System.out.println("Community cards: " + communityCards);
    }

    private void performBettingRound(int startingPosition, String roundName) {
        System.out.println("Starting " + roundName + " betting round.");
        boolean bettingRoundActive = true;
        int playersToAct = playersStillInGame();

        while (bettingRoundActive && playersToAct > 0 && playerCanPlaceBet() > 1) {
            bettingRoundActive = false;
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get((startingPosition + i) % players.size());
                if (player.isActive()) {
                    displayPlayerBets();
                    System.out.println("Community cards: " + communityCards);
                    System.out.println("Your hand: " + player.getHand());
                    System.out.println("Current bet: " + currentBet);
                    System.out.println(player.getName() + "'s turn. Current chips: " + player.getChips());

                    int playerMaxBet = player.getChips() + player.getCurrentBet();

                    if (player.getChips() == 0) {
                        System.out.println(player.getName() + " is all-in with " + player.getChips() + " chips.");
                        playersToAct--;
                        continue;
                    }

                    if (player.getCurrentBet() == currentBet) {
                        if (playerMaxBet > currentBet) {
                            System.out.println("Choose action: 1) Fold 2) Check 3) Raise");
                        } else {
                            System.out.println("Choose action: 1) Fold 2) Check");
                        }
                        int action = scanner.nextInt();
                        switch (action) {
                            case 1: // Fold
                                player.setActive(false);
                                playersToAct--;
                                break;
                            case 2: // Check
                                // Do nothing as player checks
                                playersToAct--;
                                break;
                            case 3: // Raise
                                if (playerMaxBet > currentBet) {
                                    System.out.println("Enter raise amount:");
                                    int raiseAmount = scanner.nextInt();
                                    int raiseToAmount = currentBet + raiseAmount;
                                    if (raiseToAmount > playerMaxBet) {
                                        System.out.println(
                                                "Raise amount exceeds your chips. Setting raise amount to max possible: "
                                                        + player.getChips());
                                        raiseToAmount = playerMaxBet;
                                    }
                                    currentBet = raiseToAmount;
                                    player.placeBet(currentBet - player.getCurrentBet());
                                    bettingRoundActive = true; // Another round of betting is needed
                                    playersToAct = playersStillInGame(); // Reset players to act
                                } else {
                                    System.out.println("Invalid action. You cannot raise.");
                                }
                                break;
                            default:
                                System.out.println("Invalid action. Folding by default.");
                                player.setActive(false);
                                playersToAct--;
                                break;
                        }
                    } else {
                        if (playerMaxBet > currentBet) {
                            System.out.println("Choose action: 1) Fold 2) Call 3) Raise");
                        } else {
                            System.out.println("Choose action: 1) Fold 2) Call");
                        }
                        int action = scanner.nextInt();
                        switch (action) {
                            case 1: // Fold
                                player.setActive(false);
                                playersToAct--;
                                break;
                            case 2: // Call
                                int callAmount = currentBet - player.getCurrentBet();
                                if (callAmount > player.getChips()) {
                                    System.out.println(
                                            "Call amount exceeds your chips. Setting call amount to max possible: "
                                                    + player.getChips());
                                    callAmount = player.getChips();
                                }
                                player.placeBet(callAmount);
                                playersToAct--;
                                break;
                            case 3: // Raise
                                if (playerMaxBet > currentBet) {
                                    System.out.println("Enter raise amount:");
                                    int raiseAmount = scanner.nextInt();
                                    int raiseToAmount = currentBet + raiseAmount;
                                    if (raiseToAmount > playerMaxBet) {
                                        System.out.println(
                                                "Raise amount exceeds your chips. Setting raise amount to max possible: "
                                                        + player.getChips());
                                        raiseToAmount = playerMaxBet;
                                    }
                                    currentBet = raiseToAmount;
                                    player.placeBet(currentBet - player.getCurrentBet());
                                    bettingRoundActive = true; // Another round of betting is needed
                                    playersToAct = playersStillInGame(); // Reset players to act
                                } else {
                                    System.out.println("Invalid action. You cannot raise.");
                                }
                                break;
                            default:
                                System.out.println("Invalid action. Folding by default.");
                                player.setActive(false);
                                playersToAct--;
                                break;
                        }
                    }
                }
            }
        }
    }

    private void createPots() {
        List<Player> activePlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.isActive()) {
                activePlayers.add(player);
            }
        }

        activePlayers.sort((p1, p2) -> Integer.compare(p1.getCurrentBet(), p2.getCurrentBet()));

        int previousBet = 0;
        for (int i = 0; i < activePlayers.size(); i++) {
            Player player = activePlayers.get(i);
            int bet = player.getCurrentBet();
            if (bet > previousBet) {
                Pot pot = new Pot();
                int potAmount = (bet - previousBet);
                List<Player> playersInvolved = new ArrayList<>(activePlayers.subList(i, activePlayers.size()));
                pot.addBet(potAmount, playersInvolved);
                pots.add(pot);
                previousBet = bet;
            }
        }
    }

    private void displayPlayerBets() {
        System.out.println("Current player bets:");
        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getCurrentBet());
        }
    }

    private int playersStillInGame() {
        int count = 0;
        for (Player player : players) {
            if (player.isActive()) {
                count++;
            }
        }
        return count;
    }

    private int playerCanPlaceBet(){
        int count = 0;
        for (Player player: players){
            if(player.isActive() && player.getChips() > 0){
                count++;
            }
        }
        return count;
    }

    private void distributePots() {
        int potIndex = 0; // 初始化彩池索引

        for (Pot pot : pots) {
            List<Player> candidateWinners = new ArrayList<>();
            int bestScore = -1;
            // 找到所有可能的贏家
            for (Player player : pot.getPlayers()) {
                List<Card> combinedHand = new ArrayList<>(player.getHand());
                combinedHand.addAll(communityCards);
                int score = HandEvaluator.evaluateHand(combinedHand);
                if (score > bestScore) {
                    candidateWinners.clear();
                    bestScore = score;
                    candidateWinners.add(player);
                } else if (score == bestScore) {
                    candidateWinners.add(player);
                }
            }
            if (!candidateWinners.isEmpty()) {
                if (candidateWinners.size() == 1) {
                    Player potWinner = candidateWinners.get(0);
                    if (potIndex == 0) {
                        System.out.println(
                                "Main Pot winner is: " + potWinner.getName() + " with " + pot.getAmount() + " chips.");
                    } else {
                        System.out.println("Side Pot " + potIndex + " winner is: " + potWinner.getName() + " with "
                                + pot.getAmount() + " chips.");
                    }
                    potWinner.receiveChips(pot.getAmount());
                } else {
                    // 平手的情况
                    if (potIndex == 0) {
                        System.out.println("There is a tie for the Main Pot.");
                    } else {
                        System.out.println("There is a tie for Side Pot " + potIndex + ".");
                    }
                    System.out.println("The pot of " + pot.getAmount()
                            + " chips will be divided equally among the following players:");
                    for (Player tiePlayer : candidateWinners) {
                        System.out.println(tiePlayer.getName());
                        tiePlayer.receiveChips(pot.getAmount() / candidateWinners.size()); // 平分獎池金额
                    }
                }
            }
            potIndex++; // 更新彩池索引
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }
}
