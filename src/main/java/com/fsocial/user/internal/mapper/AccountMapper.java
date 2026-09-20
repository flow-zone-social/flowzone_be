package com.fsocial.user.internal.mapper;

import com.fsocial.user.internal.dto.ProfileDTO;
import com.fsocial.user.internal.dto.ProfileResponse;
import com.fsocial.user.internal.dto.AccountRegisterRequest;
import com.fsocial.user.internal.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toEntity(AccountRegisterRequest accountDTO);
//
//    @Mapping(target = "id", source = "id")
//    @Mapping(target = "username", source = "username")
//    AccountResponse toAccountResponse(Account account);
    Account toEntity(ProfileDTO profileDTO);

    @Mapping(target = "displayName", expression = "java(com.fsocial.user.internal.util.DisplayNameUtils.build(account))")
    ProfileResponse toProfileResponse(Account account);
}
