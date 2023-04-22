public class Language {
    public static void main(String[] args) {
        System.out.println("Hey, Github, I'm a Java project ya dingus.");
        System.out.println("Please acknowledge that!");

        try {
            while (true) {
                Thread.sleep(250);
                System.out.println("I am a Java project!");
            }
        } catch (InterruptedException e) {
        }
    }
}
