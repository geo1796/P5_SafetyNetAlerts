package com.safetynet.alerts.mappers;

import com.safetynet.alerts.dto.HomeMemberDto;
import com.safetynet.alerts.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HomeMemberMapper {
    HomeMemberMapper INSTANCE = Mappers.getMapper(HomeMemberMapper.class);

    HomeMemberDto toDto(Person person);
}
