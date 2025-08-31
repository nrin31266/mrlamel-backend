package com.rin.mrlamel.feature.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rin.mrlamel.feature.ai.dto.AskResponse;
import com.rin.mrlamel.feature.ai.dto.UserDto;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DeepSeekAIService {
    UserService userService;
    WebClient webClient = WebClient.create();

    @NonFinal
    @Value("${openrouter.api-key}")
    private String openRouterApiKey;

    private String openRouterApiUrl = "https://openrouter.ai/api/v1/chat/completions";

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

        // Gộp bước extract keyword và build API endpoints
        ApiAnalysisResult analysisResult;
        try {
            analysisResult = extractKeywordAndBuildEndpoints(question, userRole);
        } catch (Exception e) {
            return "Xin lỗi, tôi không thể phân tích câu hỏi của bạn vào lúc này. Vui lòng thử lại sau.";
        }

        log.info("API Analysis Result: {}", analysisResult.toString());



//        if (analysisResult.keyword.equals("unauthorized")) {
//            return cleanedHtml(askDeepSeek(SYSTEM_CONTEXT.formatted(question) + """
//                    *Chú ý: Hãy trả lời rằng bạn không có quyền truy cập thông tin này một cách vui vẻ và lịch sự.
//                    Hoặc đưa ra những thông tin chung chung về trung tâm mà không đề cập đến thông tin cá nhân.
//                    """));
//        }

        if (analysisResult.keyword.isEmpty() || analysisResult.endpoints.isEmpty()) {
            return cleanedHtml(askDeepSeek(SYSTEM_CONTEXT.formatted(question) + """
                    Đây là câu hỏi chung chung, không cần truy vấn API, hãy trả lời trực tiếp với những thông tin bạn biết và sẵn có.
                    """));
        }

        // Gọi API và lấy dữ liệu
        StringBuilder apiResponses = new StringBuilder();
        for (String endpoint : analysisResult.endpoints) {
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
                Format kết quả bằng các thẻ HTML như <p>, <ul>, <li>, <b>, <i> Có thể css inline nếu cần.
                Sử dụng tiếng Việt để trả lời.
                Cho câu hỏi sau: "%s".
                """.formatted(combinedResponse, question);

        String finalResult = askDeepSeek(finalPrompt);
        return cleanedHtml(finalResult);
    }

    private String cleanedHtml(String html) {
        return html
                .replaceAll("(?i)Note:.*$", "") // loại bỏ note cuối chuỗi
                .replace("\n", "")              // loại bỏ ký tự xuống dòng
                .trim();
    }

    private String askDeepSeek(String question) {
        try {
            // Tạo payload cho DeepSeek API
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = Map.of(
                    "model", "deepseek/deepseek-r1-0528:free",
                    "messages", List.of(
                            Map.of("role", "user", "content", question)
                    )
            );

            String response = webClient.post()
                    .uri(openRouterApiUrl)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openRouterApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                if (!messageNode.isMissingNode()) {
                    return messageNode.path("content").asText();
                }
            }

            return "Xin lỗi, tôi không thể xử lý câu hỏi của bạn vào lúc này.";
        } catch (Exception e) {
            log.error("Error calling DeepSeek API: {}", e.getMessage());
            return "Xin lỗi, tôi không thể kết nối đến dịch vụ AI vào lúc này.";
        }
    }

    private UserDto getUserFromToken(String token) {
        User user = userService.getUserByToken(token);
        return UserDto.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail()).role(user.getRole().name()).build();
    }

    private ApiAnalysisResult extractKeywordAndBuildEndpoints(String question, String userRole) {
        String prompt = """
                ***CHỈ TRẢ VỀ JSON duy nhất, KHÔNG THÊM BẤT KỲ chữ gì khác, KHÔNG giải thích, KHÔNG mô tả, KHÔNG ví dụ***
                Bạn là hệ thống phân loại câu hỏi và xây dựng API endpoints.
                
                TRẢ VỀ JSON với cấu trúc:
                {
                    "keyword": "từ_khóa_phù_hợp",
                    "endpoints": ["url_đầy_đủ_1", "url_đầy_đủ_2", ...]
                }
                
                CÁC TỪ KHÓA CÓ THỂ:
                - "lịch học theo tuần"
                - "lịch học theo ngày"
                - "tôi là ai"
                - "" (chuỗi rỗng)
                
                QUY TẮC:
                1. Câu hỏi về lịch học trong tuần, lịch học các ngày -> "lịch học theo tuần"
                2. Câu hỏi về lịch học cụ thể ngày nào, hôm nay, ngày mai -> "lịch học theo ngày"
                3. Câu hỏi "tôi là ai", "thông tin của tôi như số điện thoại, ngày sinh, tên, ngày tham gia,..." -> "tôi là ai"
                4. Câu hỏi khác, chào hỏi, thông tin chung -> ""
                
                XÂY DỰNG ENDPOINTS:
                - "lịch học theo tuần": http://localhost:8080/api/v1/student/classes/time-table/week?weekNumber={n}
                  (n = 0: tuần này, 1: tuần sau, -1: tuần trước)
                
                - "lịch học theo ngày": http://localhost:8080/api/v1/student/classes/time-table/day?date={dateValue}
                  (hôm nay là ngày %s). dateValue định dạng ISO yyyy-MM-dd ví dụ 2024-07-01
                
                - "tôi là ai": http://localhost:8080/api/v1/auth/my
                
                - "unauthorized" hoặc "": endpoints rỗng []
                
                CÂU HỎI: "%s"
                USER ROLE: "%s"
                
                ***CHỈ TRẢ VỀ JSON duy nhất, KHÔNG THÊM BẤT KỲ chữ gì khác, KHÔNG giải thích, KHÔNG mô tả, KHÔNG ví dụ***
                """.formatted(LocalDate.now().toString(), question, userRole);

        String response = askDeepSeek(prompt);

        // Parse JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            String keyword = rootNode.path("keyword").asText("");
            List<String> endpoints = new ArrayList<>();

            JsonNode endpointsNode = rootNode.path("endpoints");
            if (endpointsNode.isArray()) {
                for (JsonNode node : endpointsNode) {
                    endpoints.add(node.asText());
                }
            }

            return new ApiAnalysisResult(keyword, endpoints);
        } catch (Exception e) {
            log.error("Error parsing API analysis result: {}", e.getMessage());
            return new ApiAnalysisResult("", new ArrayList<>());
        }
    }


    // Class để lưu kết quả phân tích
    @ToString
    private static class ApiAnalysisResult {
        String keyword;
        List<String> endpoints;

        ApiAnalysisResult(String keyword, List<String> endpoints) {
            this.keyword = keyword;
            this.endpoints = endpoints;
        }
    }
}