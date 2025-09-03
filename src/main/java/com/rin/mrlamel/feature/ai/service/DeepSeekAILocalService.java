package com.rin.mrlamel.feature.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rin.mrlamel.feature.ai.dto.UserDto;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DeepSeekAILocalService {
    UserService userService;
    WebClient webClient;
    ObjectMapper objectMapper;

    @NonFinal
    @Value("${ollama.api-url:http://localhost:11434/api/generate}")
    private String ollamaApiUrl;

    private final String modal = "deepseek-llm:7b";

    // thông tin doanh nghiệp
//    static final String SYSTEM_CONTEXT = """
//            *Bạn là trợ lý ảo của Mr.Lam TOEIC.
//            *Trung tâm Mr. Lam TOEIC Đà Nẵng: Trung tâm Mr. Lam TOEIC Đà Nẵng, thành lập năm 2014,
//            là một trong những đơn vị đào tạo tiếng Anh uy tín hàng đầu tại thành phố Đà Nẵng. Trung tâm chuyên về
//            luyện thi TOEIC với nhiều khóa học đa dạng, từ cơ bản đến nâng cao, đáp ứng nhu cầu học tập của sinh viên,
//            người đi làm và các đối tượng muốn cải thiện kỹ năng tiếng Anh để phục vụ công việc hoặc học tập. Với phương
//            pháp giảng dạy chuyên sâu, thực chiến và lộ trình học cá nhân hóa, trung tâm đã giúp nhiều học viên đạt từ 500+ đến 900+ TOEIC.
//            Hệ thống bài giảng được cập nhật liên tục dựa trên đề thi thực tế, kết hợp với môi trường học tập năng động và sự hỗ trợ
//            tận tình của đội ngũ giáo viên, trợ giảng, giúp học viên tiến bộ nhanh chóng và đạt kết quả mong muốn.
//            *Sứ mệnh: Sứ mệnh của trung tâm là lan tỏa ngôn ngữ tiếng Anh đến học sinh, sinh viên trên toàn quốc, tạo điều kiện để học viên tiếp cận môi trường học tập chất lượng với chi phí hợp lý. Trung tâm hướng tới việc nâng cao khả năng giao tiếp và cơ hội phát triển bản thân của học viên thông qua các khóa học được thiết kế chuyên sâu, cá nhân hóa lộ trình học, đồng thời cung cấp các phương pháp giảng dạy thực chiến, dễ áp dụng và đảm bảo đầu ra.
//            *Giá trị cốt lõi:
//                Trung tâm Mr. Lam TOEIC xây dựng và duy trì hoạt động dựa trên các giá trị cốt lõi, tạo nền tảng văn hóa giáo dục và phương pháp quản lý:
//                     • “Cùng nhau làm, cùng nhau hưởng”: Giá trị này khuyến khích tinh thần hợp tác giữa giáo viên, trợ giảng và học viên. Mọi thành quả đạt được là kết quả của sự phối hợp và nỗ lực chung, giúp xây dựng môi trường học tập và làm việc đoàn kết, hiệu quả.
//                     • “Until you spread your wings, you don’t know how far you can fly”: Giá trị này nhấn mạnh việc vượt qua giới hạn bản thân, khuyến khích học viên tự tin thử thách khả năng, từ đó phát triển kỹ năng tiếng Anh một cách toàn diện.
//                 Các giá trị cốt lõi này được phản ánh trực tiếp trong các hoạt động hàng ngày của trung tâm, từ thiết kế khóa học, phương pháp giảng dạy, cho tới quy trình quản lý học viên. Chúng không chỉ định hướng hành vi và thái độ của đội ngũ giảng dạy mà còn giúp học viên nhận thức được tầm quan trọng của sự cố gắng, kỷ luật và tinh thần hợp tác trong quá trình học tập.
//           *Mục tiêu và định hướng:
//                • Quản lý tập trung và hiệu quả: Hệ thống giúp quản lý thông tin học viên, giáo viên, lớp học, lịch học, điểm số và học phí một cách đồng bộ, tránh tình trạng rời rạc hoặc trùng lặp dữ liệu.
//                • Tối ưu hóa quy trình vận hành: Giảm thiểu các công việc thủ công, tiết kiệm thời gian cho quản trị viên và giáo viên, đồng thời giảm sai sót trong quản lý dữ liệu.
//                • Minh bạch và theo dõi tiến độ: Học viên, giáo viên và ban quản lý có thể truy cập thông tin cần thiết nhanh chóng, nắm bắt được tiến độ học tập, tình trạng học phí, điểm danh và kết quả học tập.
//                • Hỗ trợ ra quyết định và cải tiến chất lượng: Dữ liệu được lưu trữ và tổng hợp, phục vụ việc lập báo cáo thống kê, đánh giá hiệu quả đào tạo, từ đó đưa ra các giải pháp cải tiến phù hợp.
//                • Đáp ứng nhu cầu học viên: Hệ thống được thiết kế giúp học viên theo dõi lộ trình học tập, điểm số, kết quả thi thử, lịch học và tình trạng thanh toán học phí một cách thuận tiện và minh bạch.
//            ->Định hướng của trung tâm là phát triển một hệ thống quản lý hiện đại, linh hoạt, dễ mở rộng trong tương lai, nhằm không chỉ phục vụ cho trung tâm hiện tại mà còn tạo tiền đề mở rộng quy mô, triển khai thêm các cơ sở hoặc khóa học mới.
//           * Thông tin liên hệ của Mr.Lam TOEIC:
//                - CS1: 30 Trần Quang Diệu, Sơn Trà, Tp. Đà Nẵng
//                - CS2: 31 Trương Văn Đa, Liên Chiểu, Tp. Đà Nẵng
//                - CS3: Lô 219 Nam Kỳ Khởi Nghĩa, Hoà Hải, Ngũ Hành Sơn, Tp. Đà Nẵng
//                - Hotline: 0989.40.10.66
//                - Email: doanphangialam2012@gmail.com.
//            Yêu cầu:
//            1 Trả lời thật ngắn gọn, xúc tích, đúng ý chính.
//            2 Trả lời bằng tiếng Việt.
//            3 Format kết quả bằng các thẻ HTML và css inline.
//            4 Không đưa thông tin ngoài lề.
//            5 Không nói lại câu hỏi.
//            6 Nếu không biết câu trả lời, hãy khôn khéo xử lý để không làm mất lòng người hỏi.
//            7 Nếu có dữ liệu từ API, hãy ưu tiên sử dụng dữ liệu đó để trả lời.
//            8 Không nêu lại dữ liệu thô từ API.
//            9 Nếu không có dữ liệu từ API, hãy trả lời dựa trên kiến thức của bạn.
//            10 Luôn giữ thái độ lịch sự, thân thiện, chuyên nghiệp.
//            11 Luôn bắt đầu câu trả lời bằng lời chào.
//            Cho câu hỏi sau: "%s".
//            """;
    private static final String SYSTEM_CONTEXT = """
    Bạn là trợ lý ảo của Mr.Lam TOEIC, Trung tâm Mr. Lam TOEIC Đà Nẵng, chuyên luyện thi TOEIC (500+ đến 900+). 
    Thông tin liên hệ:
        - CS1: 30 Trần Quang Diệu, Sơn Trà, Tp. Đà Nẵng
        - CS2: 31 Trương Văn Đa, Liên Chiểu, Tp. Đà Nẵng
        - CS3: Lô 219 Nam Kỳ Khởi Nghĩa, Hoà Hải, Ngũ Hành Sơn, Tp. Đà Nẵng
        - Hotline: 0989.40.10.66
        - Email: doanphangialam2012@gmail.com.

    Hướng dẫn trả lời:
    1. Trả lời ngắn gọn, đúng ý chính.
    2. Bằng tiếng Việt.
    3. Dùng HTML + CSS inline.
    4. Không đưa thông tin ngoài lề.
    5. Không nhắc lại câu hỏi.
    6. Nếu không biết, xử lý khéo léo.
    7. Ưu tiên dữ liệu từ API, không lặp lại dữ liệu thô.
    8. Luôn bắt đầu bằng lời chào.
    
    Câu hỏi: "%s".
""";

    public Flux<String> ask(String question, String token) {
        UserDto user = getUserFromToken(token);
        String userRole = user.getRole();
       if(true){
              return askOllamaStream(SYSTEM_CONTEXT.formatted(question), "qwen2.5:1.5b");
       }
        // Gộp bước extract keyword và build API endpoints
        ApiAnalysisResult analysisResult;
        try {
            analysisResult = extractKeywordAndBuildEndpoints(question, userRole);
        } catch (Exception e) {
            return Flux.just("Xin lỗi, tôi không thể phân tích câu hỏi của bạn vào lúc này. Vui lòng thử lại sau.");
        }

        log.info("API Analysis Result: {}", analysisResult);

        // Nếu không có từ khóa hoặc endpoints, trả lời trực tiếp
        if (analysisResult.keyword.isEmpty() || analysisResult.endpoints.isEmpty()) {
            return askOllamaStream(SYSTEM_CONTEXT.formatted(question), "qwen2.5:1.5b");
//                    .map(this::cleanedHtml);
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
                return Flux.just("Xin lỗi, tôi không thể truy xuất dữ liệu từ hệ thống vào lúc này. Vui lòng thử lại sau.");
            }
        }

        String combinedResponse = apiResponses.toString();
        log.info("Combined API response: {}", combinedResponse);

        String finalPrompt =SYSTEM_CONTEXT.formatted(question) + """
                Đây là dữ liệu hệ thống được cung cấp bí mật cho bạn: %s.
                """.formatted(combinedResponse);

        return askOllamaStream(finalPrompt, modal);
    }

//    private String cleanedHtml(String html) {
//        return html
//                .replaceAll("(?i)Note:.*$", "") // loại bỏ note cuối chuỗi
//                .replace("\n", "")              // loại bỏ ký tự xuống dòng
//                .trim();
//    }

    private Flux<String> askOllamaStream(String prompt, String model) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", true
        );

        // dùng StringBuilder để tích lũy token
//        StringBuilder accumulator = new StringBuilder();

        return webClient.post()
                .uri(ollamaApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::extractContentFromOllamaChunk)
                .filter(content -> !content.isEmpty())
                .map(token -> {
//                    accumulator.append(token);
//                    String full = accumulator.toString();
//                    // bỏ markdown code fence đầu cuối
////                    full = full.replaceAll("^```(?:html)?\\s*", "").replaceAll("```$", "");
//                    return full;
                    return token.replaceAll("^```(?:html)?\\s*", "").replaceAll("```$", "");
                })

                .onErrorResume(e -> {
                    log.error("Error calling Ollama API: {}", e.getMessage());
                    return Flux.just("Xin lỗi, tôi không thể kết nối với hệ thống AI vào lúc này. Vui lòng thử lại sau.");
                });
    }


    private String extractContentFromOllamaChunk(String chunk) {
        try {
            JsonNode jsonNode = objectMapper.readTree(chunk);

            // Xử lý response format của Ollama
            if (jsonNode.has("response")) {
                return jsonNode.path("response").asText("");
            }

            // Nếu không có response field, trả về empty
            return "";
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse Ollama stream chunk: {}", chunk, e);
            return "";
        }
    }

    private UserDto getUserFromToken(String token) {
        User user = userService.getUserByToken(token);
        return UserDto.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail()).role(user.getRole().name()).build();
    }

    private ApiAnalysisResult extractKeywordAndBuildEndpoints(String question, String userRole) {
        // Ngày hiện tại để chèn vào prompt
        String today = LocalDate.now().toString();

        String prompt = """
            ***CHỈ TRẢ VỀ JSON DUY NHẤT***
            KHÔNG THÊM chữ nào khác, KHÔNG giải thích, KHÔNG ví dụ, không note, không comment.

            Bạn là hệ thống phân loại câu hỏi và xây dựng API endpoints.

            --- YÊU CẦU TRẢ VỀ ---
            {
                "keyword": "từ_khóa_phù_hợp",
                "endpoints": ["url_đầy_đủ_1", "url_đầy_ủ_2", ...]
            }

            --- CÁC TỪ KHÓA HỢP LỆ ---
            - "lịch học theo tuần"
            - "lịch học theo ngày"
            - "tôi là ai"
            - "" (chuỗi rỗng)

            --- QUY TẮC PHÂN LOẠI ---
            1. Hỏi về lịch học tuần này/tuần sau/tuần trước → "lịch học theo tuần"
            2. Hỏi về lịch học hôm nay/ngày mai/ngày cụ thể → "lịch học theo ngày"
            3. Hỏi "tôi là ai" hoặc thông tin cá nhân (tên, số điện thoại, ngày sinh, ngày tham gia, ...) → "tôi là ai"
            4. Câu hỏi khác (chào hỏi, thông tin chung, không liên quan) → ""

            --- QUY TẮC XÂY DỰNG ENDPOINT ---
            - "lịch học theo tuần":
              http://localhost:8080/api/v1/student/classes/time-table/week?weekNumber={n}
              (n = 0: tuần này, 1: tuần sau, -1: tuần trước)

            - "lịch học theo ngày":
              http://localhost:8080/api/v1/student/classes/time-table/day?date={dateValue}
              (hôm nay = %s, định dạng yyyy-MM-dd, ví dụ 2024-07-01)

            - "tôi là ai":
              http://localhost:8080/api/v1/auth/my

            - "" hoặc "unauthorized": []
            
            --- INPUT ---
            Câu hỏi: "%s"
            User role: "%s"

            ***CHỈ TRẢ VỀ JSON DUY NHẤT***
            """.formatted(today, question, userRole);

        // gọi model (gợi ý: thay deepseek-r1:8b bằng deepseek-llm:7b để nhanh hơn, không có reasoning think)
        String response = askOllamaBlocking(prompt, "deepseek-r1:8b");
        log.info("Raw API analysis response: {}", response);

        return parseApiAnalysisResponse(response);
    }




    private ApiAnalysisResult parseApiAnalysisResponse(String response) {
        try {
            // Tìm JSON trong response (có thể có text xung quanh)
            String jsonString = extractJsonFromResponse(response);

            if (jsonString == null) {
                log.warn("No JSON found in response: {}", response);
                return new ApiAnalysisResult("", new ArrayList<>());
            }

            JsonNode rootNode = objectMapper.readTree(jsonString);
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
            log.debug("Raw response that caused error: {}", response);
            return new ApiAnalysisResult("", new ArrayList<>());
        }
    }

    private String extractJsonFromResponse(String response) {

        // Làm sạch thinking process trước
        response = cleanThinkingProcess(response);
        // Tìm vị trí của ký tự { đầu tiên
        int startIndex = response.indexOf('{');
        if (startIndex == -1) {
            return null;
        }

        // Tìm vị trí của ký tự } cuối cùng
        int endIndex = response.lastIndexOf('}');
        if (endIndex == -1 || endIndex <= startIndex) {
            return null;
        }

        // Trích xuất chuỗi JSON
        return response.substring(startIndex, endIndex + 1);
    }

    private String cleanThinkingProcess(String response) {
        // Loại bỏ phần <think>...</think> nếu có
        if (response.contains("<think>") && response.contains("</think>")) {
            int thinkStart = response.indexOf("<think>");
            int thinkEnd = response.indexOf("</think>") + "</think>".length();

            // Giữ phần sau </think>
            if (thinkEnd < response.length()) {
                return response.substring(thinkEnd).trim();
            }
            return "";
        }

        // Loại bỏ phần thinking không có tag
        if (response.toLowerCase().contains("think") && response.contains("{")) {
            int jsonStart = response.indexOf('{');
            return response.substring(jsonStart);
        }

        return response;
    }


    // Blocking version for internal use where streaming isn't needed
    private String askOllamaBlocking(String prompt, String model) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        try {
            String response = webClient.post()
                    .uri(ollamaApiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response từ Ollama
            JsonNode rootNode = objectMapper.readTree(response);
            if (rootNode.has("response")) {
                return rootNode.path("response").asText();
            }

            return "{}"; // Return empty JSON if parsing fails
        } catch (Exception e) {
            log.error("Error calling Ollama API: {}", e.getMessage());
            return "{}"; // Return empty JSON on error
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