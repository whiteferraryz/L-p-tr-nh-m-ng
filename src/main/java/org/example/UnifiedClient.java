package org.example;// UnifiedClient.java
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException; // Để xử lý lỗi nhập số
import java.util.Scanner;

public class UnifiedClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12340; // Phải khớp với cổng của server

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // `true` để tự động flush
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Đã kết nối tới server đa dịch vụ.");

            while (true) {
                System.out.println("\n--- Chọn Dịch Vụ ---");
                System.out.println("1. Đảo ngược chuỗi (REVERSE)");
                System.out.println("2. Ước lượng Pi (PI_ESTIMATION)");
                System.out.println("3. Tra cứu thông tin IP (IP_INFO)");
                System.out.println("4. Thoát (EXIT)");
                System.out.print("Nhập lựa chọn của bạn (1-4): ");

                String choice = scanner.nextLine().trim();

                switch (choice.toUpperCase()) {
                    case "1":
                    case "REVERSE":
                        sendRequestAndHandleResponse("REVERSE", scanner, out, in);
                        break;
                    case "2":
                    case "PI_ESTIMATION":
                        sendRequestAndHandleResponse("PI_ESTIMATION", scanner, out, in);
                        break;
                    case "3":
                    case "IP_INFO":
                        sendRequestAndHandleResponse("IP_INFO", scanner, out, in);
                        break;
                    case "4":
                    case "EXIT":
                        out.println("EXIT"); // Gửi lệnh thoát cho server
                        System.out.println("Đang ngắt kết nối khỏi server...");
                        return; // Thoát khỏi chương trình client
                    default:
                        System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập số từ 1 đến 4.");
                        break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Lỗi: Server không tìm thấy tại địa chỉ " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Lỗi I/O khi kết nối hoặc giao tiếp với server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendRequestAndHandleResponse(String requestType, Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
        out.println(requestType); // Gửi loại yêu cầu cho server

        switch (requestType) {
            case "REVERSE":
                System.out.print("Nhập một chuỗi để đảo ngược: ");
                String message = scanner.nextLine();
                out.println(message); // Gửi chuỗi
                String reversedResponse = in.readLine(); // Nhận phản hồi
                System.out.println("Chuỗi đảo ngược: " + reversedResponse);
                break;

            case "PI_ESTIMATION":
                long N;
                while (true) {
                    System.out.print("Nhập N (>= 1.000.000) để ước lượng Pi: ");
                    try {
                        N = scanner.nextLong();
                        scanner.nextLine(); // Consume newline left-over
                        if (N < 1_000_000) {
                            System.out.println("N phải lớn hơn hoặc bằng 1.000.000.");
                        } else {
                            break; // Valid N
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Đầu vào không hợp lệ. Vui lòng nhập một số nguyên lớn.");
                        scanner.next(); // Consume the invalid input
                    }
                }
                out.println(String.valueOf(N)); // Gửi N dưới dạng chuỗi
                String piResponse = in.readLine(); // Nhận Pi
                System.out.println("Pi ước lượng: " + piResponse);
                break;

            case "IP_INFO":
                System.out.print("Nhập địa chỉ IP công cộng để tra cứu: ");
                String ip = scanner.nextLine();
                out.println(ip); // Gửi IP
                String ipInfoResponse = in.readLine(); // Nhận thông tin IP
                System.out.println("Thông tin IP: " + ipInfoResponse);
                break;

            default:
                System.out.println("Lỗi nội bộ client: Loại yêu cầu không xác định.");
                break;
        }
    }
}