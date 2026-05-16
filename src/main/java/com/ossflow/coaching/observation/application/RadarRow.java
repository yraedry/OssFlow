package com.ossflow.coaching.observation.application;

import com.ossflow.catalog.technique.domain.TechniqueFamily;

public record RadarRow(TechniqueFamily family, Long score) {}
