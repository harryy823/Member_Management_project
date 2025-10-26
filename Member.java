// Member.java
public abstract class Member {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private double baseFee;
    private int performanceRating; // 0-100

    public Member(String id, String firstName, String lastName, String email, double baseFee, int performanceRating) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.baseFee = baseFee;
        this.performanceRating = performanceRating;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getEmail() { return email; }
    public double getBaseFee() { return baseFee; }
    public int getPerformanceRating() { return performanceRating; }

    public void setFirstName(String fn) { this.firstName = fn; }
    public void setLastName(String ln) { this.lastName = ln; }
    public void setEmail(String e) { this.email = e; }
    public void setBaseFee(double f) { this.baseFee = f; }
    public void setPerformanceRating(int r) { this.performanceRating = r; }

    public String getType() { return "Member"; }

    public abstract double calculateFee();

    public String toCSV() {
        return id + "," + firstName + "," + lastName + "," + email + "," + baseFee + "," + performanceRating;
    }

    @Override
    public String toString() {
        return String.format("ID:%s Name:%s Email:%s Fee:%.2f Perf:%d", id, getFullName(), email, calculateFee(), performanceRating);
    }
}
