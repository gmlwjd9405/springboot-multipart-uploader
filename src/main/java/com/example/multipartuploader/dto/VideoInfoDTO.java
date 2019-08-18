package com.example.multipartuploader.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoInfoDTO {

    private String name;
    private String section;

    public String getName() {
        return name;
    }

    public String getSection() {
        return section;
    }
}
