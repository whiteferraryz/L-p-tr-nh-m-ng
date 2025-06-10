package org.example;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        System.out.println("Client đã kết nối từ: " + clientSocket.getInetAddress().getHostAddress());
        try {
            // Giữ kết nối mở cho đến khi client đóng hoặc có lỗi
            // Ở đây không có vòng lặp đọc/ghi, luồng sẽ chạy cho đến khi socket bị đóng
            // Hoặc bạn có thể thêm một vòng lặp nhỏ để giữ luồng sống
            // Ví dụ: while (!clientSocket.isClosed() && clientSocket.isConnected()) { Thread.sleep(100); }

            // Cách đơn giản nhất để giữ luồng bận mà không chặn main thread:
            // Đọc một byte để kiểm tra trạng thái kết nối
            // Khi client ngắt kết nối, read() sẽ trả về -1 hoặc ném IOException
            clientSocket.getInputStream().read();

        } catch (IOException e) {
            // Xảy ra khi client ngắt kết nối hoặc có lỗi I/O khác
            System.err.println("Lỗi khi xử lý client " + clientSocket.getInetAddress().getHostAddress() + ": " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                    System.out.println("Client " + clientSocket.getInetAddress().getHostAddress() + " đã ngắt kết nối.");
                }
            } catch (IOException e) {
                System.err.println("Lỗi khi đóng socket client: " + e.getMessage());
            }
        }
    }
}