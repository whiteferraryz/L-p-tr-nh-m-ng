package org.example;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UnifiedClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12340; // Phải khớp với cổng của server

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Đã kết nối tới server.");
            System.out.println("Nhấn ENTER để ngắt kết nối.");

            scanner.nextLine(); // Chờ người dùng nhấn Enter

            System.out.println("Đang ngắt kết nối khỏi server...");

        } catch (UnknownHostException e) {
            System.err.println("Lỗi: Server không tìm thấy tại địa chỉ " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Lỗi I/O khi kết nối hoặc giao tiếp với server: " + e.getMessage());
            // e.printStackTrace(); // Có thể bật lại để debug nếu cần
        }
    }
}