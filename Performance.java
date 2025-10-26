// Performance.java
public class Performance {
    private String memberId;
    private String month;
    private boolean goalAchieved;
    private String notes;

    public Performance(String memberId, String month, boolean goalAchieved, String notes) {
        this.memberId = memberId;
        this.month = month;
        this.goalAchieved = goalAchieved;
        this.notes = notes;
    }

    public String getMemberId() { return memberId; }
    public String getMonth() { return month; }
    public boolean isGoalAchieved() { return goalAchieved; }
    public String getNotes() { return notes; }

    public String toCSV() {
        return memberId + "," + month + "," + (goalAchieved?"1":"0") + "," + notes.replaceAll("\\r?\\n", " ");
    }

    public static Performance fromCSV(String line) {
        String[] parts = line.split(",",4);
        if (parts.length < 3) return null;
        String mid = parts[0].trim();
        String month = parts[1].trim();
        boolean achieved = parts[2].trim().equals("1");
        String notes = parts.length >=4 ? parts[3].trim() : "";
        return new Performance(mid, month, achieved, notes);
    }

    @Override
    public String toString() {
        return String.format("%s - %s Achieved:%s Notes:%s", memberId, month, goalAchieved?"Yes":"No", notes);
    }
}
