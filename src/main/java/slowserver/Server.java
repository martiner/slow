package slowserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Server {

    static final int PORT = 8080;

    private static final String RESPONSE = "Hello world Hello world";

    public static void main(String... args) throws IOException {

        final ServerSocket server = new ServerSocket(PORT);
        System.out.println("Listening on " + PORT);

        while (true) {
            final Socket socket = server.accept();
            new Thread() {
                @Override
                public void run() {
                    final int port = socket.getPort();
                    System.out.println("Connected: " + port);
                    try (
                            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            final OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream())
                    ) {
                        String line;
                        do {
                            line = reader.readLine();

                        }
                        while (!"".equals(line));
                        writer.write("HTTP/1.0 200 OK\n");
                        writer.write("Content-type: text/plain\n");
                        writer.write("Content-length: " + RESPONSE.length() + "\n");
                        writer.write("\n");

                        for (char c: RESPONSE.toCharArray()) {
                            writer.write(c);
                            writer.flush();
                            TimeUnit.SECONDS.sleep(5);
                        }
                        System.out.println("Finished: " + port);
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Error: "  + port + " " + e.getMessage());
                    }
                }
            }.start();
        }
    }
}
