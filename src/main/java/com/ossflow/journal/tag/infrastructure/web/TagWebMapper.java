package com.ossflow.journal.tag.infrastructure.web;

import com.ossflow.journal.tag.domain.Tag;
import com.ossflow.journal.tag.infrastructure.web.dto.TagResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagWebMapper {

    TagResponse toResponse(Tag tag);
}
