import Server.UDP_Server;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        String pathToLog = args[0]; //C:\\Java\\Files\\ServerLog.txt

        Scanner sc = new Scanner(System.in);
        System.out.print("Введите порт для сервера: ");
        int port = sc.nextInt();
        UDP_Server server = new UDP_Server("localhost", port, pathToLog);
        server.run();
    }
}