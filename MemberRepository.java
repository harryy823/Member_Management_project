// MemberRepository.java
import java.io.*;
import java.util.*;

public class MemberRepository {
    private List<Member> members;
    private Map<String, Member> idIndex;
    private Map<String, List<Performance>> perfHistory;

    public MemberRepository() {
        members = new ArrayList<>();
        idIndex = new HashMap<>();
        perfHistory = new HashMap<>();
    }

    public List<Member> getMembers() { return members; }

    private void rebuildIndex() {
        idIndex.clear();
        for (Member m : members) idIndex.put(m.getId().toUpperCase(), m);
    }

    // Load members from CSV: id,first,last,email,baseFee,perf,type[,trainer]
    public void loadFromFile(String filename) throws IOException {
        members.clear();
        File f = new File(filename);
        if (!f.exists()) throw new FileNotFoundException(filename + " not found");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) continue;
                String id = parts[0].trim();
                String first = parts[1].trim();
                String last = parts[2].trim();
                String email = parts[3].trim();
                double baseFee = Double.parseDouble(parts[4].trim());
                int perf = Integer.parseInt(parts[5].trim());
                String type = parts[6].trim();
                if (type.equalsIgnoreCase("Regular")) {
                    members.add(new RegularMember(id, first, last, email, baseFee, perf));
                } else if (type.equalsIgnoreCase("Trainer")) {
                    String trainerName = parts.length >= 8 ? parts[7].trim() : "";
                    members.add(new TrainerMember(id, first, last, email, baseFee, perf, trainerName));
                } else {
                    members.add(new RegularMember(id, first, last, email, baseFee, perf));
                }
            }
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number in file: " + e.getMessage());
        }
        rebuildIndex();
    }

    public void saveToFile(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (Member m : members) pw.println(m.toCSV() + "," + m.getType());
        }
    }

    public void addMember(Member m) {
        members.add(m);
        idIndex.put(m.getId().toUpperCase(), m);
    }

    public Member findById(String id) {
        if (id == null) return null;
        return idIndex.get(id.toUpperCase());
    }

    public List<Member> findByName(String name) {
        List<Member> res = new ArrayList<>();
        for (Member m : members) {
            if (m.getFullName().toLowerCase().contains(name.toLowerCase())) res.add(m);
        }
        return res;
    }

    public boolean deleteMember(String id) {
        Member t = findById(id);
        if (t != null) {
            members.remove(t);
            idIndex.remove(id.toUpperCase());
            return true;
        }
        return false;
    }

    public boolean updateMember(Member updated) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId().equalsIgnoreCase(updated.getId())) {
                members.set(i, updated);
                rebuildIndex();
                return true;
            }
        }
        return false;
    }

    // Sorting
    public void sortByFirstName() {
        Collections.sort(members, Comparator.comparing(Member::getFirstName, String.CASE_INSENSITIVE_ORDER));
        rebuildIndex();
    }

    public void sortById() {
        Collections.sort(members, Comparator.comparing(Member::getId, String.CASE_INSENSITIVE_ORDER));
        rebuildIndex();
    }

    public void sortByPerformanceDesc() {
        Collections.sort(members, Comparator.comparingInt(Member::getPerformanceRating).reversed());
        rebuildIndex();
    }

    // Searching
    public Member linearSearchById(String id) {
        for (Member m : members) if (m.getId().equalsIgnoreCase(id)) return m;
        return null;
    }

    public Member binarySearchById(String id) {
        sortById();
        int left = 0, right = members.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int cmp = members.get(mid).getId().compareToIgnoreCase(id);
            if (cmp == 0) return members.get(mid);
            if (cmp < 0) left = mid + 1; else right = mid - 1;
        }
        return null;
    }

    // Performance history
    public void addPerformanceRecord(String memberId, Performance p) {
        perfHistory.computeIfAbsent(memberId.toUpperCase(), k -> new ArrayList<>()).add(p);
    }

    public List<Performance> getPerformanceHistory(String memberId) {
        return perfHistory.getOrDefault(memberId.toUpperCase(), Collections.emptyList());
    }

    public void savePerformanceHistory(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (List<Performance> list : perfHistory.values()) {
                for (Performance p : list) pw.println(p.toCSV());
            }
        }
    }

    public void loadPerformanceHistory(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Performance p = Performance.fromCSV(line);
                if (p != null) addPerformanceRecord(p.getMemberId(), p);
            }
        }
    }

    // Letter generation
    public String generateLetter(String memberId, String month, boolean appreciation) throws IOException {
        Member m = findById(memberId);
        if (m == null) throw new IllegalArgumentException("Member not found");
        File dir = new File("letters");
        if (!dir.exists()) dir.mkdirs();
        String type = appreciation ? "appreciation" : "reminder";
        String fname = String.format("letters/%s_%s_%s.txt", memberId, month.replaceAll("\\s+","_"), type);
        try (PrintWriter pw = new PrintWriter(new FileWriter(fname))) {
            if (appreciation) pw.println(LetterTemplates.appreciation(m, month));
            else pw.println(LetterTemplates.reminder(m, month));
        }
        return fname;
    }
}
