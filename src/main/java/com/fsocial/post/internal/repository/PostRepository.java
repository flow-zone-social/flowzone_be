package com.fsocial.post.internal.repository;

import com.fsocial.post.internal.dto.PostFeedRow;
import com.fsocial.post.internal.dto.PostStatisticsDTO;
import com.fsocial.post.internal.dto.PostStatisticsLongDateDTO;
import com.fsocial.post.internal.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {

    List<Post> findByOwnerId(String userId);

    @Query("""
            select p from Post p
            where lower(p.text) like lower(concat('%', :keyword, '%'))
            """)
    List<Post> findByTextContainingIgnoreCase(@Param("keyword") String keyword);

    @Query("""
            select p from Post p
            where lower(p.text) like lower(concat('%', :keyword, '%'))
            """)
    List<Post> findByTextContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            select p from Post p
            where p.id not in :excludedIds
            order by p.createDatetime desc
            """)
    List<Post> findByIdNotInOrderByCreateDatetimeDesc(@Param("excludedIds") List<String> excludedIds,
                                                      Pageable pageable);

    @Query("""
            select p from Post p
            where p.owner.id in :userIds and p.id not in :excludedIds
            order by p.createDatetime desc
            """)
    List<Post> findByOwnerIdInAndIdNotInOrderByCreateDatetimeDesc(@Param("userIds") List<String> userIds,
                                                                  @Param("excludedIds") List<String> excludedIds,
                                                                  Pageable pageable);

    // ---- Feed recommendation queries (BRD) ----

    @Query("""
            select p from Post p
            where array_contains(p.tags, :tag) = true and p.id not in :excludedIds and p.status = true
            order by p.globalScore desc
            """)
    List<Post> findByTagAndIdNotIn(@Param("tag") String tag,
                                   @Param("excludedIds") List<String> excludedIds,
                                   Pageable pageable);

    @Query("""
            select p from Post p
            where array_contains(p.tags, :tag) = true and p.id not in :excludedIds and p.status = true
              and p.createDatetime >= :since
            order by p.globalScore desc
            """)
    List<Post> findByTagAndIdNotInSince(@Param("tag") String tag,
                                        @Param("excludedIds") List<String> excludedIds,
                                        @Param("since") LocalDateTime since,
                                        Pageable pageable);

    @Query("""
            select p from Post p
            where array_intersects(p.tags, :tags) = true and p.id not in :excludedIds and p.status = true
            order by p.globalScore desc
            """)
    List<Post> findByTagsInAndIdNotIn(@Param("tags") List<String> tags,
                                      @Param("excludedIds") List<String> excludedIds,
                                      Pageable pageable);

    @Query("""
            select p from Post p
            where array_intersects(p.tags, :tags) = true and p.id not in :excludedIds and p.status = true
              and p.createDatetime >= :since
            order by p.globalScore desc
            """)
    List<Post> findByTagsInAndIdNotInSince(@Param("tags") List<String> tags,
                                           @Param("excludedIds") List<String> excludedIds,
                                           @Param("since") LocalDateTime since,
                                           Pageable pageable);

    @Query("""
            select p from Post p
            where p.id not in :excludedIds and p.status = true
            order by p.globalScore desc
            """)
    List<Post> findTopByGlobalScore(@Param("excludedIds") List<String> excludedIds, Pageable pageable);

    @Query("""
            select p from Post p
            where p.id not in :excludedIds and p.status = true
              and p.createDatetime >= :since
            order by p.globalScore desc
            """)
    List<Post> findTopByGlobalScoreSince(@Param("excludedIds") List<String> excludedIds,
                                         @Param("since") LocalDateTime since,
                                         Pageable pageable);

    // ---- Statistics ----

    @Query(value = """
            select cast(extract(hour from create_datetime) as varchar) as hour, count(*) as count
            from post
            where create_datetime between :startDate and :endDate
            group by 1
            order by 1
            """, nativeQuery = true)
    List<Object[]> countByCreatedAtByHoursRaw(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query(value = """
            select date_trunc('day', create_datetime) as date, count(*) as count
            from post
            where create_datetime between :startDate and :endDate
            group by 1
            order by 1
            """, nativeQuery = true)
    List<Object[]> countByDateRaw(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    default List<PostStatisticsDTO> countByCreatedAtByHours(LocalDateTime startDate, LocalDateTime endDate) {
        return countByCreatedAtByHoursRaw(startDate, endDate).stream()
                .map(r -> PostStatisticsDTO.builder()
                        .hour((String) r[0])
                        .count(((Number) r[1]).intValue())
                        .build())
                .toList();
    }

    default List<PostStatisticsLongDateDTO> countByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return countByDateRaw(startDate, endDate).stream()
                .map(r -> PostStatisticsLongDateDTO.builder()
                        .date(((java.sql.Timestamp) r[0]).toLocalDateTime())
                        .count(((Number) r[1]).intValue())
                        .build())
                .toList();
    }

    // ---- Score updates (thay cho MongoTemplate .inc/.set) ----

    /** clearAutomatically: ScoreUpdateConsumer đọc lại shareCount ngay sau khi tăng. */
    @Modifying(clearAutomatically = true)
    @Query("update Post p set p.shareCount = p.shareCount + 1 where p.id = :postId")
    int incrementShareCount(@Param("postId") String postId);

    @Modifying
    @Query("update Post p set p.rawEngagement = :raw, p.globalScore = :global where p.id = :postId")
    int updateScores(@Param("postId") String postId,
                     @Param("raw") double rawEngagement,
                     @Param("global") double globalScore);

    Window<List<Post>> findByOwnerId(String ownerId, Sort sort, ScrollPosition position);

    // ---- Posts by owner, enriched (profile/timeline) ----

    /**
     * owner/like/comment đều gộp vào 1 truy vấn qua subquery; media lấy riêng theo postIds (xem getPostsByUser).
     * Phải viết "select new PostFeedRow(...)" rõ ràng: cơ chế rewrite tự động của Spring Data (bỏ "new") chỉ áp dụng
     * khi select toàn bộ entity HOẶC chỉ các property đơn giản — không hoạt động khi trộn entity (p) với subquery
     * scalar như ở đây. Thiếu "new", Hibernate trả về Tuple thô và Spring không tìm được converter sang record
     * (ConverterNotFoundException: TupleBackedMap -> PostFeedRow) khi thực thi với dữ liệu thật.
     */
    @Query("""
            select new com.fsocial.post.internal.dto.PostFeedRow(
                p, a.displayName, a.avatar,
                (select count(pl) from PostLike pl where pl.postId = p.id),
                (select count(c) from Comment c where c.post.id = p.id and c.parent is null),
                (select count(pl2) from PostLike pl2 where pl2.postId = p.id and pl2.userId = :requesterId)
            )
            from Post p
            join p.owner a
            where a.id = :ownerId
            order by p.createDatetime desc
            """)
    List<PostFeedRow> findPostsByOwner(@Param("ownerId") String ownerId,
                                       @Param("requesterId") String requesterId,
                                       Pageable pageable);

    // ---- Posts by id set, enriched (feed candidate enrichment) ----

    /** Giống findPostsByOwner nhưng lọc theo danh sách id (candidate pool của feed cá nhân hóa, nhiều owner khác nhau). */
    @Query("""
            select new com.fsocial.post.internal.dto.PostFeedRow(
                p, a.displayName, a.avatar,
                (select count(pl) from PostLike pl where pl.postId = p.id),
                (select count(c) from Comment c where c.post.id = p.id and c.parent is null),
                (select count(pl2) from PostLike pl2 where pl2.postId = p.id and pl2.userId = :requesterId)
            )
            from Post p
            join p.owner a
            where p.id in :postIds
            """)
    List<PostFeedRow> findPostsByIds(@Param("postIds") List<String> postIds,
                                     @Param("requesterId") String requesterId);
}
