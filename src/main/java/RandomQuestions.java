import java.util.Random;

public class RandomQuestions {
    public static void main(String[] args) {
        String[] questions = {"What is your name?", "What is your age?", "What is your favorite color?"};

        Random random = new Random();
        for (int i = 0; i < questions.length; i++) {
            int randomIndex = random.nextInt(questions.length);
            String question = questions[randomIndex];
            System.out.println(question);
        }
    }
}