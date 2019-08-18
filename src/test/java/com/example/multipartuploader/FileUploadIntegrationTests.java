package com.example.multipartuploader;

import com.example.multipartuploader.dto.VideoInfoDTO;
import com.example.multipartuploader.storage.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private StorageService storageService;

    @LocalServerPort
    private int port;

    @Test
    public void shouldUploadFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("SampleVideo_360x240_2mb.mp4", getClass());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        ResponseEntity<String> response = this.restTemplate.postForEntity("/", map, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().toString())
                .startsWith("http://localhost:" + this.port + "/");
        then(storageService).should().store(any(MultipartFile.class));
    }

    @Test
    public void shouldUploadMultipartData() throws Exception {
        ClassPathResource resource = new ClassPathResource("SampleVideo_360x240_2mb.mp4", getClass());
        VideoInfoDTO videoInfoDTO = new VideoInfoDTO("비디오 이름", "애니메이션");

//        MultipartBodyBuilder builder = new MultipartBodyBuilder();
//        builder.part("file", resource);
//        builder.part("videoInfo", videoInfoDTO);
//        MultiValueMap<String, HttpEntity<?>> map = builder.build();

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        map.add("videoInfo", videoInfoDTO);
        ResponseEntity<String> response = this.restTemplate.postForEntity("/multipartUpload", map, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
//        assertThat(response.getHeaders().getLocation().toString())
//                .startsWith("http://localhost:" + this.port + "/multipartUpload");
        then(storageService).should().store(any(MultipartFile.class));
    }

    @Test
    public void shouldDownloadFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("SampleVideo_360x240_2mb.mp4", getClass());
        given(this.storageService.loadAsResource("SampleVideo_360x240_2mb.mp4")).willReturn(resource);

        ResponseEntity<String> response = this.restTemplate
                .getForEntity("/files/{filename}", String.class, "SampleVideo_360x240_2mb.mp4");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"SampleVideo_360x240_2mb.mp4\"");
    }

}
