// Main.java
import java.util.Scanner;
import javax.swing.*;

public class Main {
    private static MemberRepository repo = new MemberRepository();

    public static void main(String[] args) {
        // Try to load default files if present
        try {
            repo.loadFromFile("member_data.csv");
            System.out.println("Loaded " + repo.getMembers().size() + " members from member_data.csv");
        } catch (Exception e) {
            System.out.println("No default member data loaded: " + e.getMessage());
        }

        try {
            repo.loadPerformanceHistory("performance_data.csv");
            System.out.println("Loaded performance history.");
        } catch (Exception e) {
            System.out.println("No default performance data loaded: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Choose interface mode:\n1. GUI\n2. Text-Based (Console)");
        String choice = sc.nextLine().trim();
        if (choice.equals("1")) {
            SwingUtilities.invokeLater(() -> {
                GUIManager g = new GUIManager(repo);
                g.initGUI();
            });
        } else {
            TextUI.runConsole(repo);
        }
    }
}
