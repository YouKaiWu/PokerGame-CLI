import java.util.*;

class Pot {
    private int amount;
    private List<Player> players;

    public Pot() {
        amount = 0;
        players = new ArrayList<>();
    }

    public void addBet(int bet, List<Player> playersInvolved) {
        amount += bet * playersInvolved.size();
        players.addAll(playersInvolved);
    }

    public int getAmount() {
        return amount;
    }

    public List<Player> getPlayers() {
        return players;
    }
}