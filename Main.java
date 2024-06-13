import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Alice", 100000));
        players.add(new Player("Bob", 10000));
        players.add(new Player("Charlie", 1000));

        PokerGame game = new PokerGame(players, 5, 10);
        game.startGame();

        for (Player player : game.getPlayers()) {
            System.out.println(player.getName() + "'s hand: " + player.getHand());
        }

        System.out.println("Community Cards: " + game.getCommunityCards());
    }
}
