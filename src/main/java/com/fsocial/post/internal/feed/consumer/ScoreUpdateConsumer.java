package com.fsocial.post.internal.feed.consumer;

import com.fsocial.post.internal.feed.dto.InteractionEvent;
import com.fsocial.comment.internal.repository.CommentRepository;
import com.fsocial.post.internal.repository.PostLikeRepository;
import com.fsocial.post.internal.repository.PostRepository;
import com.fsocial.post.internal.feed.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreUpdateConsumer {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final ScoringService scoringService;

    @RabbitListener(queues = "#{@scoreUpdateQueue.name}")
    @Transactional
    public void handleInteractionEvent(InteractionEvent event) {
        if (event == null || event.getPostId() == null) return;

        String postId = event.getPostId();

        if ("SHARE".equals(event.getActionType())) {
            postRepository.incrementShareCount(postId);
        }

        postRepository.findById(postId).ifPresent(post -> {
            Integer rawCommentCount = commentRepository.countByPostId(postId);
            int commentCount = rawCommentCount == null ? 0 : rawCommentCount;
            int likeCount = postLikeRepository.countByPostId(postId);

            double rawEngagement = scoringService.calculateRawEngagement(post, likeCount, commentCount);
            double newScore = scoringService.calculateGlobalScore(post, likeCount, commentCount);

            postRepository.updateScores(postId, rawEngagement, newScore);

            log.debug("Updated global_score={} raw_engagement={} for post {}", newScore, rawEngagement, postId);
        });
    }
}
