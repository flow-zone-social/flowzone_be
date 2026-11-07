package com.fsocial.media.internal.service;

import com.cloudinary.Cloudinary;
import com.fsocial.media.api.MediaApi;
import com.fsocial.media.internal.mapper.AttachmentMapper;
import com.fsocial.media.internal.repository.AttachmentsRepository;
import com.fsocial.media.internal.util.MediaUploadUtils;
import com.fsocial.user.internal.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MediaBeanWiringTest {

    @Test
    void mediaModuleBeanGraphIsAcyclic() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(AttachmentsServiceImpl.class, UploadMediaImpl.class,
                    MediaUploadUtils.class, MediaApiImpl.class);

            ctx.registerBean("attachmentsRepository", AttachmentsRepository.class,
                    () -> mock(AttachmentsRepository.class));
            ctx.registerBean("attachmentMapper", AttachmentMapper.class,
                    () -> mock(AttachmentMapper.class));
            ctx.registerBean("accountRepository", AccountRepository.class,
                    () -> mock(AccountRepository.class));
            ctx.registerBean("entityManager", EntityManager.class,
                    () -> mock(EntityManager.class));
            ctx.registerBean("cloudinary", Cloudinary.class,
                    () -> mock(Cloudinary.class));

            ctx.refresh();

            assertThat(ctx.getBean(MediaApi.class)).isInstanceOf(MediaApiImpl.class);
            assertThat(ctx.getBean(AttachmentsService.class)).isInstanceOf(AttachmentsServiceImpl.class);
            assertThat(ctx.getBean(UploadMedia.class)).isInstanceOf(UploadMediaImpl.class);
        }
    }
}
