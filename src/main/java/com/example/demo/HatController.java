package com.example.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/chat")
public class HatController {

    // 關鍵：這會自動去讀取 application.properties 裡的設定
    @Value("${gemini.api.key}")
    private String apiKey;

    @PostMapping("/send")
    public String handleChat(@RequestBody Map<String, String> request) {
        // 使用從環境變數注入的 apiKey
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
        
        String incomingMsg = request.getOrDefault("content", "");
        String userGender = request.getOrDefault("userGender", "female");
        String targetGender = request.getOrDefault("targetGender", "male");
        
        String prompt = String.format(
            "對方傳來這句話：%s。請以「%s」的身份，回覆「%s」。請提供一句充滿愛意、幽默且貼心的回覆，不需要多餘的解釋，只要直接給出建議的語句即可。",
            incomingMsg, 
            ("male".equals(targetGender) ? "男友" : "女友"),
            ("male".equals(userGender) ? "男生" : "女生")
        );
        
        Map<String, Object> body = new HashMap<>();
        body.put("contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", prompt)})});
        
        try {
            RestTemplate rest = new RestTemplate();
            Map<String, Object> response = rest.postForObject(url, body, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "軍師暫時離線 (API 錯誤): " + e.getMessage();
        }
    }
}