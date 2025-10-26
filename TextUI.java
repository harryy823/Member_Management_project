// TextUI.java
import java.util.*;
public class TextUI {
    public static void runConsole(MemberRepository repo) {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("\n===== Member Management System (Console) =====");
            System.out.println("1. Load records from file");
            System.out.println("2. Add new member and save to a new file");
            System.out.println("3. Update member and save to a new file");
            System.out.println("4. Delete member and save to a new file");
            System.out.println("5. Load new file to view / query member details");
            System.out.println("6. Sort members (by first name)");
            System.out.println("7. Search by ID");
            System.out.println("8. Add performance record");
            System.out.println("9. View performance history");
            System.out.println("10. Generate letter for member");
            System.out.println("11. Save performance history to file");
            System.out.println("12. Exit");
            System.out.print("Choose: "); String c = sc.nextLine().trim();
            switch(c) {
                case "1": 
                    System.out.print("Filename: "); 
                    String f1=sc.nextLine().trim(); 
                    try { repo.loadFromFile(f1); System.out.println("Loaded."); } 
                    catch(Exception e){ System.out.println("Error: " + e.getMessage()); } 
                    break;
                case "2": 
                    addMemberConsole(repo, sc); 
                    System.out.print("Save as filename: "); 
                    String s1=sc.nextLine().trim(); 
                    try { repo.saveToFile(s1); System.out.println("Saved."); } 
                    catch(Exception e){ System.out.println("Save error: " + e.getMessage()); } 
                    break;
                case "3": 
                    updateMemberConsole(repo, sc); 
                    System.out.print("Save as filename: "); 
                    String s2=sc.nextLine().trim(); 
                    try { repo.saveToFile(s2); System.out.println("Saved."); } 
                    catch(Exception e){ System.out.println("Save error: " + e.getMessage()); } 
                    break;
                case "4": 
                    System.out.print("ID to delete: "); 
                    String idd=sc.nextLine().trim(); 
                    if (repo.deleteMember(idd)) System.out.println("Deleted."); else System.out.println("Not found."); 
                    System.out.print("Save as filename: "); 
                    String s3=sc.nextLine().trim(); 
                    try { repo.saveToFile(s3); System.out.println("Saved."); } 
                    catch(Exception e){ System.out.println("Save error: " + e.getMessage()); } 
                    break;
                case "5": 
                    System.out.print("Filename to view: "); 
                    String fv=sc.nextLine().trim(); 
                    try { MemberRepository temp=new MemberRepository(); temp.loadFromFile(fv); temp.getMembers().forEach(System.out::println);} 
                    catch(Exception e){ System.out.println("Error: " + e.getMessage()); } 
                    break;
                case "6": 
                    repo.sortByFirstName(); 
                    System.out.println("Sorted by first name."); 
                    break;
                case "7": 
                    System.out.print("ID: "); 
                    String sid=sc.nextLine().trim(); 
                    Member mm = repo.findById(sid); 
                    System.out.println(mm==null?"Not found":mm); 
                    break;
                case "8": 
                    addPerformanceConsole(repo, sc); 
                    break;
                case "9": 
                    System.out.print("ID for history: "); 
                    String hid=sc.nextLine().trim(); 
                    List<Performance> history = repo.getPerformanceHistory(hid);
                    if (history.isEmpty()) System.out.println("No performance records found.");
                    else history.forEach(System.out::println);
                    break;
                case "10": 
                    System.out.print("ID: "); 
                    String lid=sc.nextLine().trim(); 
                    System.out.print("Month (e.g., 2025-09): "); 
                    String lmon=sc.nextLine().trim(); 
                    System.out.print("Appreciation? (y/n): "); 
                    String ly=sc.nextLine().trim(); 
                    try { String path = repo.generateLetter(lid,lmon,ly.equalsIgnoreCase("y")); System.out.println("Letter created: " + path); } 
                    catch(Exception e){ System.out.println("Error: " + e.getMessage()); } 
                    break;
                case "11": 
                    System.out.print("Performance filename to save (performance_data.csv): "); 
                    String pf=sc.nextLine().trim(); 
                    try { repo.savePerformanceHistory(pf); System.out.println("Perf saved."); } 
                    catch(Exception e){ System.out.println("Error saving perf: " + e.getMessage()); } 
                    break;
                case "12": 
                    exit=true; 
                    System.out.println("Exiting."); 
                    break;
                default: 
                    System.out.println("Invalid."); 
                    break;
            }
        }
    }

    private static void addMemberConsole(MemberRepository repo, Scanner sc) {
        try {
            System.out.print("ID: "); String id=sc.nextLine().trim();
            System.out.print("First: "); String fn=sc.nextLine().trim();
            System.out.print("Last: "); String ln=sc.nextLine().trim();
            System.out.print("Email: "); String email=sc.nextLine().trim();
            System.out.print("Base fee: "); double fee = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Performance (0-100): "); int perf = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Type (Regular/Trainer): "); String type=sc.nextLine().trim();
            if (type.equalsIgnoreCase("Trainer")) {
                System.out.print("Trainer name: "); String tn=sc.nextLine().trim();
                repo.addMember(new TrainerMember(id, fn, ln, email, fee, perf, tn));
            } else {
                repo.addMember(new RegularMember(id, fn, ln, email, fee, perf));
            }
            System.out.println("Added."); 
        } catch (NumberFormatException e) { System.out.println("Invalid number."); }
    }

    private static void updateMemberConsole(MemberRepository repo, Scanner sc) {
        System.out.print("ID to update: "); String id=sc.nextLine().trim();
        Member m = repo.findById(id);
        if (m==null) { System.out.println("Not found."); return; }
        System.out.println("Found: " + m);
        System.out.print("New first (blank to keep): "); String fn=sc.nextLine().trim(); if (!fn.isEmpty()) m.setFirstName(fn);
        System.out.print("New last (blank to keep): "); String ln=sc.nextLine().trim(); if (!ln.isEmpty()) m.setLastName(ln);
        System.out.print("New email (blank to keep): "); String em=sc.nextLine().trim(); if (!em.isEmpty()) m.setEmail(em);
        System.out.print("New base fee (blank to keep): "); String fs=sc.nextLine().trim(); if (!fs.isEmpty()) try { m.setBaseFee(Double.parseDouble(fs)); } catch(Exception e) { System.out.println("Invalid fee."); }
        System.out.print("New perf (blank to keep): "); String ps=sc.nextLine().trim(); if (!ps.isEmpty()) try { m.setPerformanceRating(Integer.parseInt(ps)); } catch(Exception e) { System.out.println("Invalid perf."); }
        if (m instanceof TrainerMember) { System.out.print("New trainer (blank to keep): "); String tn=sc.nextLine().trim(); if (!tn.isEmpty()) ((TrainerMember)m).setTrainerName(tn); }
        repo.updateMember(m);
        System.out.println("Updated."); 
    }

    private static void addPerformanceConsole(MemberRepository repo, Scanner sc) {
        try {
            System.out.print("Member ID: "); String id = sc.nextLine().trim();
            System.out.print("Month (e.g., 2025-09): "); String month = sc.nextLine().trim();
            System.out.print("Achieved? (y/n): "); String a = sc.nextLine().trim();
            System.out.print("Notes: "); String notes = sc.nextLine().trim();
            boolean achieved = a.equalsIgnoreCase("y");
            Performance p = new Performance(id, month, achieved, notes);
            repo.addPerformanceRecord(id, p);
            System.out.println("Perf recorded."); 
        } catch (Exception e) { System.out.println("Error adding performance: " + e.getMessage()); }
    }
}
