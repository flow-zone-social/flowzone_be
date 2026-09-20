package com.fsocial.auth.internal.service;

public interface BanService {
    void ban(String token);
    void unBan(String token);
}
