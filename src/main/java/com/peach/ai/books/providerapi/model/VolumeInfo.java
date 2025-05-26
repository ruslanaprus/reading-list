package com.peach.ai.books.providerapi.model;

import java.util.List;
import lombok.Data;

@Data
public class VolumeInfo {
    private String title;
    private List<String> authors;
    private Integer pageCount;
    private Double averageRating;
    private String description;
}
