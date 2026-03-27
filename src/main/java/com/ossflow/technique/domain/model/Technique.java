package com.ossflow.technique.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Technique {
    private Long id;
    private String name;
    private TechniqueCategory category;
    private String description;

    private String youtubeUrl;
    private Belt minimumBelt;
    private Modality modality;

    private Position startPosition;
    private Position endPosition;
}