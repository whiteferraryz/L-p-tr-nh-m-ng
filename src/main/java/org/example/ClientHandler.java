package org.example;// ClientHandler.java
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Random;
import java.util.regex.Pattern;
import org.json.JSONObject; // Cần thêm thư viện json.jar

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    // Các biến Pattern cho IP Lookup (có thể đặt static trong lớp Server hoặc Handler)
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        System.out.println("Client đã kết nối từ: " + clientSocket.getInetAddress().getHostAddress());
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true); // `true` để tự động flush

            String requestType;
            while ((requestType = in.readLine()) != null) { // Đọc loại yêu cầu từ client
                System.out.println("Nhận yêu cầu '" + requestType + "' từ client: " + clientSocket.getInetAddress().getHostAddress());

                switch (requestType.toUpperCase()) { // Chuyển sang chữ hoa để xử lý nhất quán
                    case "REVERSE":
                        handleStringReversal();
                        break;
                    case "PI_ESTIMATION":
                        handlePiEstimation();
                        break;
                    case "IP_INFO":
                        handleIpInfoLookup();
                        break;
                    case "EXIT": // Client gửi lệnh EXIT để đóng kết nối
                        System.out.println("Client " + clientSocket.getInetAddress().getHostAddress() + " đã yêu cầu đóng kết nối.");
                        return; // Thoát khỏi vòng lặp và đóng socket
                    default:
                        out.println("Lỗi: Yêu cầu dịch vụ không hợp lệ.");
                        System.err.println("Yêu cầu không hợp lệ từ client " + clientSocket.getInetAddress().getHostAddress() + ": " + requestType);
                        break;
                }
            }
        } catch (IOException e) {
            // Xử lý lỗi khi mất kết nối đột ngột hoặc lỗi I/O khác
            System.err.println("Lỗi khi xử lý client " + clientSocket.getInetAddress().getHostAddress() + ": " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
                System.out.println("Client " + clientSocket.getInetAddress().getHostAddress() + " đã ngắt kết nối.");
            } catch (IOException e) {
                System.err.println("Lỗi khi đóng tài nguyên client: " + e.getMessage());
            }
        }
    }

    // --- Các phương thức xử lý dịch vụ ---

    private void handleStringReversal() throws IOException {
        String clientString = in.readLine(); // Đọc chuỗi cần đảo ngược
        if (clientString != null) {
            System.out.println("REVERSE: Nhận chuỗi '" + clientString + "'");
            String reversedString = new StringBuilder(clientString).reverse().toString();
            out.println(reversedString);
            System.out.println("REVERSE: Đã gửi chuỗi đảo ngược '" + reversedString + "'");
        }
    }

    private void handlePiEstimation() throws IOException {
        try {
            long N = Long.parseLong(in.readLine()); // Đọc N
            System.out.println("PI_ESTIMATION: Nhận N = " + N);

            if (N < 1_000_000) {
                System.out.println("Cảnh báo: N nhỏ hơn 1.000.000. Ước lượng Pi có thể không chính xác.");
            }

            long circlePoints = 0;
            Random random = new Random();

            for (long i = 0; i < N; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();
                if (x * x + y * y <= 1) {
                    circlePoints++;
                }
            }
            //số điểm trong hình tròn/tổng số điểm => pi = 4 x (số điểm trong hình tròn/ tổng số điểm)
            double estimatedPi = 4.0 * circlePoints / N;
            out.println(String.valueOf(estimatedPi)); // Gửi Pi dưới dạng chuỗi
            System.out.println("PI_ESTIMATION: Đã gửi Pi ước lượng: " + estimatedPi);
        } catch (NumberFormatException e) {
            out.println("Lỗi: N không hợp lệ.");
            System.err.println("PI_ESTIMATION: Lỗi định dạng N từ client: " + e.getMessage());
        }
    }

    private void handleIpInfoLookup() throws IOException {
        String ipAddress = in.readLine(); // Đọc IP address
        System.out.println("IP_INFO: Nhận IP '" + ipAddress + "'");

        String response;
        if (isValidIP(ipAddress)) {
            if (isPrivateIP(ipAddress)) {
                response = "Lỗi: Đây là địa chỉ IP riêng tư. Vui lòng cung cấp một IP công cộng.";
            } else {
                response = getIpInfo(ipAddress);
            }
        } else {
            response = "Lỗi: Định dạng địa chỉ IP không hợp lệ.";
        }
        out.println(response);
        System.out.println("IP_INFO: Đã gửi phản hồi: " + response);
    }

    // --- Các hàm phụ trợ cho IP Lookup ---
    private boolean isValidIP(String ip) {
        return IP_ADDRESS_PATTERN.matcher(ip).matches();
    }

    private boolean isPrivateIP(String ip) {

        if (ip.startsWith("10.") || ip.startsWith("172.16.") || ip.startsWith("172.17.") ||
                ip.startsWith("172.18.") || ip.startsWith("172.19.") || ip.startsWith("172.20.") ||
                ip.startsWith("172.21.") || ip.startsWith("172.22.") || ip.startsWith("172.23.") ||
                ip.startsWith("172.24.") || ip.startsWith("172.25.") || ip.startsWith("172.26.") ||
                ip.startsWith("172.27.") || ip.startsWith("172.28.") || ip.startsWith("172.29.") ||
                ip.startsWith("172.30.") || ip.startsWith("172.31.") || ip.startsWith("192.168.")) {
            return true;
        }
        return false;
    }

    private String getIpInfo(String ip) {
        String apiUrl = "http://ip-api.com/json/" + ip;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 giây timeout
            connection.setReadTimeout(5000);    // 5 giây timeout

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }
                JSONObject jsonResponse = new JSONObject(result.toString());
                if ("success".equals(jsonResponse.optString("status"))) { // Dùng optString để tránh lỗi nếu key không tồn tại
                    return "IP: " + jsonResponse.optString("query") +
                            ", Quốc gia: " + jsonResponse.optString("country") +
                            ", Thành phố: " + jsonResponse.optString("city") +
                            ", ISP: " + jsonResponse.optString("isp");
                } else {
                    return "Lỗi API: " + jsonResponse.optString("message", "Lỗi không xác định từ API");
                }
            } else {
                return "Lỗi HTTP: " + responseCode + " - " + connection.getResponseMessage();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi gọi API IP (" + apiUrl + "): " + e.getMessage());
            return "Lỗi Server: Không thể truy xuất thông tin IP. " + e.getMessage();
        }
    }
}