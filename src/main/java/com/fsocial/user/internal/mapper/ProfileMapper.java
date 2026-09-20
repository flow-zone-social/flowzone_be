package com.fsocial.user.internal.mapper;

import com.fsocial.user.internal.dto.AccountRegisterRequest;
import com.fsocial.user.internal.dto.ProfileRegisterRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileRegisterRequest toProfileRegister(AccountRegisterRequest request);
}
