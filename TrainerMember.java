// TrainerMember.java
public class TrainerMember extends Member {
    private String trainerName;

    public TrainerMember(String id, String firstName, String lastName, String email, double baseFee, int performanceRating, String trainerName) {
        super(id, firstName, lastName, email, baseFee, performanceRating);
        this.trainerName = trainerName;
    }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String tn) { this.trainerName = tn; }

    @Override
    public double calculateFee() {
        double fee = getBaseFee() + 20.0;
        if (getPerformanceRating() >= 85) fee *= 0.85;
        return fee;
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ",Trainer," + trainerName;
    }

    @Override
    public String toString() {
        return super.toString() + " Trainer:" + trainerName;
    }

    @Override
    public String getType() { return "Trainer"; }
}
