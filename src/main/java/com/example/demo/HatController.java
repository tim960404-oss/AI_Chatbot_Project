package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class HatController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @PostMapping("/send")
    public String handleChat(@RequestBody Map<String, String> request) {
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
            if (e.getMessage().contains("429")) {
                return "軍師正在深思熟慮，請稍候 30 秒再問喔！";
            }
            return "軍師暫時離線: " + e.getMessage();
        }
    }
}
