// LetterTemplates.java
public class LetterTemplates {
    public static String appreciation(Member m, String month) {
        return "Dear " + m.getFullName() + ",\\n\\n" +
               "Congratulations on achieving your fitness goals in " + month + ".\\n" +
               "We are proud of your dedication and encourage you to keep up the great work.\\n\\n" +
               "Best regards,\\nMMS Team";
    }

    public static String reminder(Member m, String month) {
        return "Dear " + m.getFullName() + ",\\n\\n" +
               "Our records show you did not meet your fitness target in " + month + ".\\n" +
               "Please contact your trainer to plan for improvement sessions.\\n\\n" +
               "Best regards,\\nMMS Team";
    }
}
