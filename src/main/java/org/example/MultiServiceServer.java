package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServiceServer {
    private static final int PORT = 12340; // Cổng chung cho tất cả các dịch vụ

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Máy chủ đã khởi động trên cổng " + PORT);
            System.out.println("Đang chờ kết nối từ các client...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Chờ client kết nối
                // Tạo một luồng mới để xử lý client này
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi động máy chủ: " + e.getMessage());
        }
    }
}