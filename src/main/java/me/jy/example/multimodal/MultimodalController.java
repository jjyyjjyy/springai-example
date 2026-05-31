package me.jy.example.multimodal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category 3: Multimodality (Vision & Audio)
 * Demonstrates how to send multimedia inputs (images, audio, etc.) alongside text prompts
 * to Google Gemini using Spring AI 2.0.0-SNAPSHOT.
 */
@RestController
@RequestMapping("/api/multimodal")
public class MultimodalController {

    private final ChatClient chatClient;

    public MultimodalController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone().build();
    }

    /**
     * Example 9: Image Analysis (Vision)
     * Demonstrates passing an image URL dynamically to Gemini using the media() method of ChatClient.
     * The model processes both the image pixels and the text instructions to generate a response.
     */
    @PostMapping("/image")
    public String analyzeImage(@RequestBody(required = false) ImageAnalysisRequest request) {
        String url = (request != null && request.imageUrl() != null) ? request.imageUrl() : "https://images.unsplash.com/photo-1579546929518-9e396f3cc809";
        String pr = (request != null && request.prompt() != null) ? request.prompt() : "Describe the colors and feelings evoked by this abstract image.";

        try {
            UrlResource imageResource = new UrlResource(url);
            return this.chatClient.prompt()
                .user(u -> u.text(pr)
                    .media(MimeTypeUtils.IMAGE_JPEG, imageResource))
                .call()
                .content();
        } catch (Exception e) {
            return "Error loading or processing image: " + e.getMessage();
        }
    }

    /**
     * Example 10: Audio Input Description
     * Gemini models support direct ingestion of audio files (e.g. mp3, wav).
     * This example demonstrates passing a remote MP3 audio file resource and asking the LLM to summarize/transcribe it.
     */
    @PostMapping("/audio")
    public String analyzeAudio(@RequestBody(required = false) AudioAnalysisRequest request) {
        String url = (request != null && request.audioUrl() != null) ? request.audioUrl() : "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
        String pr = (request != null && request.prompt() != null) ? request.prompt() : "Describe the music in this audio clip, including the tempo, instruments, and vibe.";

        try {
            UrlResource audioResource = new UrlResource(url);
            return this.chatClient.prompt()
                .user(u -> u.text(pr)
                    .media(MimeTypeUtils.parseMimeType("audio/mp3"), audioResource))
                .call()
                .content();
        } catch (Exception e) {
            return "Error loading or processing audio: " + e.getMessage();
        }
    }

    public record ImageAnalysisRequest(String imageUrl, String prompt) {
    }

    public record AudioAnalysisRequest(String audioUrl, String prompt) {
    }
}
