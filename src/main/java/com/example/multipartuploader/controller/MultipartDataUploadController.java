package com.example.multipartuploader.controller;

import com.example.multipartuploader.dto.VideoInfoDTO;
import com.example.multipartuploader.storage.StorageService;
import com.example.multipartuploader.storage.exception.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class MultipartDataUploadController {

    private final StorageService storageService;

    @Autowired
    public MultipartDataUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * local 의 multipartData (DTO json + 영상) 업로드 요청
     */
    @GetMapping("/multipartUpload")
    public void requestUploadMultipartData() throws IOException {
        ClassPathResource resource = new ClassPathResource("SampleVideo_360x240_2mb.mp4", getClass());
        VideoInfoDTO videoInfoDTO = new VideoInfoDTO("비디오 이름", "애니메이션");

//        MultipartBodyBuilder builder = new MultipartBodyBuilder();
//        builder.part("file", resource);
//        builder.part("videoInfo", videoInfoDTO);
//        MultiValueMap<String, HttpEntity<?>> map = builder.build();

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        map.add("videoInfo", videoInfoDTO);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("/multipartUpload", map, String.class);
    }

    /**
     * multipartData 업로드 POST 요청에 대한 처리
     */
    @PostMapping("/multipartUpload")
    public String handleMultipartDataUpload(@RequestPart("file") MultipartFile file,
                                            @RequestPart("videoInfo") VideoInfoDTO videoInfoDTO,
                                            RedirectAttributes redirectAttributes) {
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename()
                        + "\n check video info: " + videoInfoDTO.getName() + videoInfoDTO.getSection() + "!");

        System.out.println("------- " + file.getOriginalFilename());
        System.out.println("------- " + videoInfoDTO.getName());
        System.out.println("------- " + videoInfoDTO.getSection());

        return "redirect:/";
    }

}

