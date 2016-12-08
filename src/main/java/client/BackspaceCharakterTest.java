package client;

/**
 * Created by Fabrice Vanner on 06.12.2016.
 */
public class BackspaceCharakterTest {
        public static void main(String[] args) throws InterruptedException {
            System.out.println("Wdsfsdfsdfdsfsdfds<");

            Thread.sleep(3000);
            System.out.print(String.format("\033[2J"));
        }
    }
