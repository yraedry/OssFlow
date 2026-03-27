package com.ossflow.technique.infra.adapter.in.web.dto;

import com.ossflow.technique.domain.model.Belt;
import com.ossflow.technique.domain.model.Modality;
import com.ossflow.technique.domain.model.TechniqueCategory;
import lombok.Data;

@Data
public class CreateTechniqueRequest {
    private String name;
    private TechniqueCategory category;
    private String description;
    private String youtubeUrl;
    private Belt minimumBelt;
    private Modality modality;
    private Long startPositionId;
}