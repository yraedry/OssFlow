package com.ossflow.coaching.observation.domain;

import com.ossflow.catalog.technique.domain.TechniqueFamily;

public record RadarRow(TechniqueFamily family, Long score) {}
