package com.fsocial.post.internal.feed.repository;

import com.fsocial.post.internal.feed.dto.HashtagSuggestion;
import com.fsocial.post.internal.feed.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, String> {

    @Modifying
    @Query(value = """
            insert into hashtag(id, name, usage_count, created_at, updated_at)
            select gen_random_uuid()::text, t, count(*), now(), now()
            from post, unnest(tags) as t
            where status = true
            group by t
            on conflict (name) do update set usage_count = excluded.usage_count, updated_at = now()
            """, nativeQuery = true)
    void syncCounts();

    @Modifying
    @Query(value = """
            delete from hashtag
            where name not in (select distinct unnest(tags) from post where status = true)
            """, nativeQuery = true)
    void deleteUnused();

    @Query("""
            SELECT new com.fsocial.post.internal.feed.dto.HashtagSuggestion(h.id, h.name, h.usageCount)
            FROM Hashtag h
            WHERE h.deleted = false AND h.name LIKE :name%
            """)
    List<HashtagSuggestion> findSuggestionByName(@Param("name") String name);

    Optional<Hashtag> findByIdAndDeletedFalse(String id);

    Optional<Hashtag> findByNameIgnoreCase(String name);

    List<Hashtag> findAllByDeletedFalseOrderByUsageCountDesc();
}
