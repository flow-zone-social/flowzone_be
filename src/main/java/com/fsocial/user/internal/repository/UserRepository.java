package com.fsocial.user.internal.repository;

import com.fsocial.user.api.ActorSnapshotDTO;
import com.fsocial.user.internal.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("select a from User a left join fetch a.role where a.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    long countByUsername(String username);
    long countByEmail(String email);
    long countByUsernameOrEmail(String username, String email);

    @Query(value = """
            select cast(extract(hour from created_at) as int) as h, count(*) as c
            from users where created_at between :startDate and :endDate
            group by 1 order by 1
            """, nativeQuery = true)
    List<Object[]> countByCreatedAtByHoursRaw(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query(value = """
            select to_char(created_at, 'YYYY-MM-DD') as d, count(*) as c
            from users where created_at between :startDate and :endDate
            group by 1 order by 1
            """, nativeQuery = true)
    List<Object[]> countByCreatedAtByDateRaw(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    default List<HourCountResult> countByCreatedAtByHours(LocalDateTime startDate, LocalDateTime endDate) {
        return countByCreatedAtByHoursRaw(startDate, endDate).stream()
                .map(r -> new HourCountResult(((Number) r[0]).intValue(), ((Number) r[1]).intValue()))
                .toList();
    }

    default List<DateCountResult> countByCreatedAtByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return countByCreatedAtByDateRaw(startDate, endDate).stream()
                .map(r -> new DateCountResult((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }

    record HourCountResult(Integer _id, Integer count) {}
    record DateCountResult(String _id, Long count) {}

    @Query("select new com.fsocial.user.api.ActorSnapshotDTO(a.id, a.displayName, a.avatar) from User a where a.id = :userId")
    Optional<ActorSnapshotDTO> findOwnerById(@Param("userId") String userId);

    /** Batch lookup — tránh N+1 khi build danh sách post. */
    @Query("select new com.fsocial.user.api.ActorSnapshotDTO(a.id, a.displayName, a.avatar) from User a where a.id in :userIds")
    List<ActorSnapshotDTO> findOwnersByIdIn(@Param("userIds") List<String> userIds);

    @Query("""
            select a from User a
            where lower(a.displayName) like lower(concat('%', :keyword, '%'))
               or lower(a.firstName)   like lower(concat('%', :keyword, '%'))
               or lower(a.lastName)    like lower(concat('%', :keyword, '%'))
               or lower(a.email)       like lower(concat('%', :keyword, '%'))
            """)
    List<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
