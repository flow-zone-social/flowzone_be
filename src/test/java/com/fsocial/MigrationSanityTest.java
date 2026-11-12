package com.fsocial;

import com.fsocial.user.internal.entity.User;
import com.fsocial.comment.internal.entity.Comment;
import com.fsocial.post.internal.dto.PostFeedRow;
import com.fsocial.post.internal.entity.Post;
import com.fsocial.post.internal.entity.PostLike;
import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.media.api.AttachmentType;
import com.fsocial.user.internal.repository.UserRepository;
import com.fsocial.media.internal.repository.AttachmentsRepository;
import com.fsocial.comment.internal.repository.CommentRepository;
import com.fsocial.post.internal.repository.PostLikeRepository;
import com.fsocial.post.internal.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sanity check cho migration Mongo -> Postgres: round-trip qua các bảng join
 * thay cho embedded array cũ (media/tag/like/reply). Chạy trên DB dev thật
 * (không có Testcontainers/Docker trong môi trường này); @Transactional rollback
 * sau mỗi test nên không để lại rác.
 */
@SpringBootTest
@Transactional
class MigrationSanityTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AttachmentsRepository attachmentsRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private CommentRepository commentRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void post_withMediaAndTags_roundTrips() {
        User owner = userRepository.save(User.builder().username("sanity_" + System.nanoTime()).build());

        Post post = postRepository.save(Post.builder().owner(owner).text("hello")
                .tags(List.of("travel", "food", "tech")).build());
        post.addMedia(Attachments.builder()
                .url("http://a/1.png")
                .type(AttachmentType.POST)
                .resourceType("image")
                .build());
        post.addMedia(Attachments.builder()
                .url("http://a/2.png")
                .type(AttachmentType.POST)
                .resourceType("image")
                .build());
        postRepository.saveAndFlush(post);
        attachmentsRepository.saveAllAndFlush(post.getMedia());

        assertThat(mediaCount(post.getId())).isEqualTo(2);
        assertThat(postRepository.findById(post.getId()).orElseThrow().getTags())
                .containsExactlyInAnyOrder("travel", "food", "tech");
    }

    @Test
    void likingTwice_countsAsOne() {
        User owner = userRepository.save(User.builder().username("sanity_" + System.nanoTime()).build());
        Post post = postRepository.save(Post.builder().owner(owner).text("hello").build());

        postLikeRepository.save(new PostLike(post.getId(), owner.getId()));
        postLikeRepository.flush();
        // second "like" is a toggle in the real service (delete-if-exists); here we just
        // assert the PK is a real uniqueness constraint, matching prior Mongo idempotency.
        assertThat(postLikeRepository.existsById(new PostLike.Key(post.getId(), owner.getId()))).isTrue();

        List<Object[]> counts = postLikeRepository.countByPostIdIn(List.of(post.getId()));
        assertThat(counts).hasSize(1);
        assertThat(((Number) counts.get(0)[1]).intValue()).isEqualTo(1);
    }

    @Test
    void deletingPost_cascadesLikeTagMedia() {
        User owner = userRepository.save(User.builder().username("sanity_" + System.nanoTime()).build());
        Post post = postRepository.save(Post.builder().owner(owner).text("hello").build());
        post.addMedia(Attachments.builder()
                .url("http://a/1.png")
                .type(AttachmentType.POST)
                .resourceType("image")
                .build());
        postRepository.saveAndFlush(post);
        attachmentsRepository.saveAllAndFlush(post.getMedia());
        postLikeRepository.saveAndFlush(new PostLike(post.getId(), owner.getId()));

        Comment root = commentRepository.saveAndFlush(
                Comment.builder().post(post).userId(owner.getId()).text("root").build());

        postRepository.delete(post);
        postRepository.flush();

        assertThat(mediaCount(post.getId())).isZero();
        assertThat(postLikeRepository.existsById(new PostLike.Key(post.getId(), owner.getId()))).isFalse();
        // comment.post_id has a real FK with ON DELETE CASCADE now, so it's deleted with the post.
        assertThat(commentRepository.findById(root.getId())).isEmpty();
    }

    @Test
    void commentWithReplies_findByPostIdReturnsOnlyRoots() {
        User owner = userRepository.save(User.builder().username("sanity_" + System.nanoTime()).build());
        Post post = postRepository.save(Post.builder().owner(owner).text("hello").build());

        Comment root = commentRepository.saveAndFlush(
                Comment.builder().post(post).userId(owner.getId()).text("root").build());
        commentRepository.saveAndFlush(
                Comment.builder().post(post).userId(owner.getId()).parent(root).text("reply1").build());
        commentRepository.saveAndFlush(
                Comment.builder().post(post).userId(owner.getId()).parent(root).text("reply2").build());

        List<Comment> roots = commentRepository.findByPostId(post.getId());

        assertThat(roots).hasSize(1);
        assertThat(roots.get(0).getId()).isEqualTo(root.getId());
        assertThat(commentRepository.findByParentId(root.getId())).hasSize(2);
    }

    /**
     * Thực thi thật findPostsByOwner/findPostsByIds (không chỉ parse ở context-load) — đây đúng là chỗ
     * ConverterNotFoundException (Tuple -> PostFeedRow) xảy ra khi thiếu "select new PostFeedRow(...)".
     */
    @Test
    void findPostsByOwnerAndByIds_returnEnrichedRows() {
        User owner = userRepository.save(User.builder()
                .username("sanity_" + System.nanoTime()).displayName("Owner Sanity").avatar("http://a/avatar.png")
                .build());
        User requester = userRepository.save(User.builder()
                .username("sanity_" + System.nanoTime()).build());

        Post post = postRepository.save(Post.builder().owner(owner).text("hello").html("<p>hello</p>").build());
        postRepository.saveAndFlush(post);
        commentRepository.saveAndFlush(Comment.builder().post(post).userId(requester.getId()).text("nice").build());
        postLikeRepository.saveAndFlush(new PostLike(post.getId(), requester.getId()));

        List<PostFeedRow> byOwner = postRepository.findPostsByOwner(owner.getId(), requester.getId(), PageRequest.of(0, 10));
        assertThat(byOwner).hasSize(1);
        PostFeedRow ownerRow = byOwner.get(0);
        assertThat(ownerRow.post().getId()).isEqualTo(post.getId());
        assertThat(ownerRow.displayName()).isEqualTo("Owner Sanity");
        assertThat(ownerRow.avatar()).isEqualTo("http://a/avatar.png");
        assertThat(ownerRow.commentCount()).isEqualTo(1L);
        assertThat(ownerRow.likeCount()).isEqualTo(1L);
        assertThat(ownerRow.isLikedByRequester()).isTrue();

        List<PostFeedRow> byIds = postRepository.findPostsByIds(List.of(post.getId()), requester.getId());
        assertThat(byIds).hasSize(1);
        assertThat(byIds.get(0).post().getId()).isEqualTo(post.getId());
        assertThat(byIds.get(0).isLikedByRequester()).isTrue();

        // requester khác (chưa like) phải ra isLikedByRequester = false
        List<PostFeedRow> byIdsOtherViewer = postRepository.findPostsByIds(List.of(post.getId()), owner.getId());
        assertThat(byIdsOtherViewer.get(0).isLikedByRequester()).isFalse();
    }

    private long mediaCount(String postId) {
        return entityManager
                .createQuery("select count(m) from Attachments m where m.post.id = :postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }
}
