// RegularMember.java
public class RegularMember extends Member {

    public RegularMember(String id, String firstName, String lastName, String email, double baseFee, int performanceRating) {
        super(id, firstName, lastName, email, baseFee, performanceRating);
    }

    @Override
    public double calculateFee() {
        if (getPerformanceRating() >= 80) return getBaseFee() * 0.9; // 10% discount
        if (getPerformanceRating() < 40) return getBaseFee() * 1.05; // 5% surcharge
        return getBaseFee();
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ",Regular";
    }

    @Override
    public String getType() { return "Regular"; }
}
