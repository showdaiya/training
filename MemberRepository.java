package com.example.large.membersystem.repository;

import com.example.large.membersystem.domain.member.MemberEntity;
import java.util.List;

public interface MemberRepository {

    List<MemberEntity> findAll();

    MemberEntity findById(long memberId);

    void save(MemberEntity member);
}
