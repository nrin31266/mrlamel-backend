package com.rin.mrlamel.feature.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rin.mrlamel.feature.ai.dto.UserDto;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class LlamaAIService {
    UserService userService;
    ChatClient chatClient;
    WebClient webClient = WebClient.create();

    // thông tin doanh nghiệp
    static final String SYSTEM_CONTEXT = """
            Bạn là trợ lý ảo của Mr.Lam TOEIC.
            Trung tâm Mr. Lam TOEIC Đà Nẵng.
            Thông tin cơ bản về Mr.Lam TOEIC:
                - CS1: 30 Trần Quang Diệu, Sơn Trà, Tp. Đà Nẵng
                - CS2: 31 Trương Văn Đa, Liên Chiểu, Tp. Đà Nẵng
                - CS3: Lô 219 Nam Kỳ Khởi Nghĩa, Hoà Hải, Ngũ Hành Sơn, Tp. Đà Nẵng
                - Hotline: 0989.40.10.66
                - Email: doanphangialam2012@gmail.com.
            Yêu cầu:
            - Trả lời ngắn gọn, xúc tích, đúng ý chính.
            - Trả lời bằng tiếng Việt.
            - Format kết quả bằng các thẻ HTML như <p>, <ul>, <li>, <b> ...
            - Không đưa thông tin ngoài lề.
            Cho câu hỏi sau: "%s".
            """;

    public String ask(String question, String token) {
        UserDto user = getUserFromToken(token);
        String userRole = user.getRole();
        String keyword;
        try {
            keyword = extractKeyword(question, userRole).toLowerCase().trim();
        } catch (Exception e) {
            return "Xin lỗi, tôi không thể phân tích câu hỏi của bạn vào lúc này. Vui lòng thử lại sau.";
        }
        log.info("Extracted keyword: {}", keyword);
        if (keyword.equals("unauthorized")) {
            return cleanedHtml(ask(SYSTEM_CONTEXT.formatted(question) + """
                    *Chú ý: Hãy trả lời rằng bạn không có quyền truy cập thông tin này một cách vui vẻ và lịch sự.
                    Hoặc đưa ra những thông tin chung chung về trung tâm mà không đề cập đến thông tin cá nhân.
                    """));
        }
        if (keyword.isEmpty()) {
            return cleanedHtml(ask(SYSTEM_CONTEXT.formatted(question) + """
                    Đây là câu hỏi chung chung, không cần truy vấn API, hãy trả lời trực tiếp với những thông tin bạn biết và sẵn có.
                    """));
        }
        String analysis;
        try {
            analysis = analyzeQuestion(question, keyword);
        } catch (Exception e) {
            return "Xin lỗi, tôi không thể phân tích câu hỏi của bạn vào lúc này. Vui lòng thử lại sau.";
        }
        log.info("Analysis result: {}", analysis);
        ObjectMapper objectMapper = new ObjectMapper();
        String type;
        List<String> endpoints = new ArrayList<>();
        try {
            type = objectMapper.readTree(analysis).get("type").asText();
            JsonNode endpointsNode = objectMapper.readTree(analysis).get("endpoints");
            if (endpointsNode != null && endpointsNode.isArray()) {
                for (JsonNode node : endpointsNode) {
                    endpoints.add(node.asText());
                }
            }

        } catch (Exception e) {
            return "Xin lỗi, tôi không thể hiểu câu hỏi của bạn. Vui lòng thử lại sau.";
        }
        if (type.isEmpty()) {
            return "Xin lỗi, tôi không thể phân tích câu hỏi của bạn vào lúc này. Vui lòng thử lại sau.";
        }
        if (type.equals("api") && endpoints.isEmpty()) {
            return "Xin lỗi, tôi không thể trích xuất API từ câu hỏi của bạn. Vui lòng thử lại sau.";
        }

        if (type.equals("chat") || endpoints.isEmpty()) {
            return cleanedHtml(ask(SYSTEM_CONTEXT.formatted(question) + """
                    Đây là câu hỏi chung chung, nó đã không thể trích được API để trả lời. Hãy trả lời trực tiếp với những thông tin bạn biết và sẵn có.
                    """));
        }
        // Gọi API và lấy dữ liệu
        StringBuilder apiResponses = new StringBuilder();
        for (String endpoint : endpoints) {
            try {
                String apiResponse = webClient.get()
                        .uri(endpoint)
                        .headers(headers -> headers.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                apiResponses.append(apiResponse).append("\n");
            } catch (Exception e) {
                return "Xin lỗi, tôi không thể truy xuất dữ liệu từ hệ thống vào lúc này. Vui lòng thử lại sau.";
            }
        }
        String combinedResponse = apiResponses.toString();
        log.info("Combined API response: {}", combinedResponse);
        String finalPrompt = question + """
                Từ dữ liệu sau: %s
                Hãy trả lời câu hỏi một cách chính xác, ngắn gọn, xúc tích nhưng đầy đủ thông tin dựa trên dữ liệu đã cho.
                Format kết quả bằng các thẻ HTML như <p>, <ul>, <li>, <b>
                Sử dụng tiếng Việt để trả lời.
                Cho câu hỏi sau: "%s".
                """.formatted(combinedResponse, question);

        String finalResult = ask(finalPrompt);
        return cleanedHtml(finalResult);
    }

    private String cleanedHtml(String html) {
        return html
                .replaceAll("(?i)Note:.*$", "") // loại bỏ note cuối chuỗi
                .replace("\n", "")              // loại bỏ ký tự xuống dòng
                .trim();
    }

    private String ask(String question) {
        return chatClient.prompt().user(question).call().content();
    }

    private UserDto getUserFromToken(String token) {
        User user = userService.getUserByToken(token);
        return UserDto.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail()).role(user.getRole().name()).build();
    }

    private String extractKeyword(String question, String userRole) {
        String prompt = """
                ***CHỈ TRẢ VỀ TỪ KHÓA duy nhất, KHÔNG THÊM BẤT KỲ chữ gì khác, KHÔNG giải thích, KHÔNG mô tả, KHÔNG ví dụ***
                Bạn là hệ thống phân loại câu hỏi. CHỈ trả về MỘT trong các từ khóa sau:
                - "lịch học theo tuần"
                - "lịch học theo ngày"
                - "tôi là ai"
                - "unauthorized"
                - "" (chuỗi rỗng)
                
                QUY TẮC:
                1. Câu hỏi về lịch học trong tuần, lịch học các ngày -> "lịch học theo tuần"
                2. Câu hỏi về lịch học cụ thể ngày nào, hôm nay, ngày mai -> "lịch học theo ngày"
                3. Câu hỏi "tôi là ai", "thông tin của tôi như số điện thoại, ngày sinh, tên, ngày tham gia,..." -> "tôi là ai"
                4. Nếu user có role "%s" KHÔNG được phép truy cập thông tin -> "unauthorized"
                5. Câu hỏi khác, chào hỏi, thông tin chung -> ""
                
                CÂU HỎI: "%s"
                TỪ KHÓA:
                ***CHỈ TRẢ VỀ TỪ KHÓA duy nhất, KHÔNG THÊM BẤT KỲ chữ gì khác, KHÔNG giải thích, KHÔNG mô tả, KHÔNG ví dụ***
                
                """.formatted(userRole, question);

        String response = chatClient.prompt().user(prompt).call().content();
        assert response != null;
        if (response.startsWith("\"") && response.endsWith("\"")) {
            response = response.substring(1, response.length() - 1);
        }
        return response.trim().toLowerCase();
    }

    private String analyzeQuestion(String question, String keyword) {
        if (!apiMap.containsKey(keyword)) {
            return "{\"type\": \"chat\", \"endpoints\": []}";
        }

        Map<String, Object> apiInfo = apiMap.get(keyword);
        String endpointTemplate = (String) apiInfo.get("endpoint");
        Map<String, String> params = (Map<String, String>) apiInfo.get("params");
        String instructions = (String) apiInfo.get("instructions");

        // Nếu không có param -> trả về luôn
        if (params.isEmpty()) {
            String fullUrl = endpointTemplate;
            return """
                    {
                        "type": "api",
                        "endpoints": ["%s"]
                    }
                    """.formatted(fullUrl);
        }

        // Nếu có param, tạo prompt để AI build full URL
        String prompt = """
                ***CHỈ TRẢ VỀ JSON duy nhất, KHÔNG THÊM BẤT KỲ chữ gì khác, KHÔNG giải thích, KHÔNG mô tả, KHÔNG ví dụ***               
                Phân tích câu hỏi và tạo URL đầy đủ.
                Câu hỏi: "%s"
                Keyword: "%s"
                
                Template API: %s
                Tham số cần điền: %s
                Hướng dẫn: %s
                
                Yêu cầu:
                1. Điền tất cả param vào URL đầy đủ dạng:
                   http://localhost:8080/api/...?param1=giá_trị&param2=giá_trị
                   * Có thể tạo nhiều URL nếu cần thiết (ví dụ: hom nay + 3 ngày tiếp theo -> 4 URL)
                2. Trả về JSON duy nhất:
                {
                    "type": "api",
                    "endpoints": ["URL_API_đầy_đủ", "URL_API_đầy_đủ_nếu_có_nhiều_hơn_1"]
                }
                ***CHỈ TRẢ VỀ JSON duy nhất, KHÔNG THÊM BẤT KỲ chữ gì khác, KHÔNG giải thích, KHÔNG mô tả, KHÔNG ví dụ***       
                """.formatted(question, keyword, endpointTemplate, params, instructions);

        String response = chatClient.prompt().user(prompt).call().content();

        // Dọn sạch JSON: loại bỏ chữ thừa trước/sau JSON
        if (response != null) {
            int first = response.indexOf("{");
            int last = response.lastIndexOf("}");
            if (first != -1 && last != -1 && last > first) {
                response = response.substring(first, last + 1).trim();
            } else {
                response = "{\"type\": \"chat\", \"endpoints\": []}";
            }
        } else {
            response = "{\"type\": \"chat\", \"endpoints\": []}";
        }

        return response;
    }


    private static final String BASE_URL = "http://localhost:8080/api";

    private static final Map<String, Map<String, Object>> apiMap = Map.of(
            "lịch học theo tuần", Map.of(
                    "endpoint", BASE_URL + "/v1/student/classes/time-table/week",
                    "params", Map.of(
                            "weekNumber", "0 (tuần hiện tại), 1 (1 tuần sau), -1 (1 tuần trước), n tuần tiếp theo (n endpoints) "
                    ),
                    "instructions", "Truyền weekNumber dựa theo câu hỏi, ví dụ: 'Lịch học tuần này' -> weekNumber=0"
            ),
            "lịch học theo ngày", Map.of(
                    "endpoint", BASE_URL + "/v1/student/classes/time-table/day",
                    "params", Map.of(
                            "date", "yyyy-MM-dd, ví dụ hôm nay: " + LocalDate.now()
                    ),
                    "instructions", "Truyền date dựa theo câu hỏi, ví dụ: 'Lịch học hôm nay' -> date=2025-08-31, n ngày tiếp theo (n endpoints)"
            ),
            "tôi là ai", Map.of(
                    "endpoint", BASE_URL + "/v1/auth/my",
                    "params", Map.of(),
                    "instructions", "Không cần param"
            )
    );


}
