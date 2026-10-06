#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Phase A mechanical migration: com.fsocial (flat) -> feature-based modules.
Moves files, rewrites package decl, rewrites FQCN literals, synthesizes imports
for bare same-old-package / wildcard-imported references. No behavior change.
"""
import os, re, sys

ROOT = os.path.dirname(os.path.abspath(__file__))
MAIN = os.path.join(ROOT, "src", "main", "java", "com", "fsocial")
TEST = os.path.join(ROOT, "src", "test", "java", "com", "fsocial")

# ---------------------------------------------------------------------------
# old relative path (posix, relative to .../com/fsocial) -> new relative path
# ---------------------------------------------------------------------------
M = {}

def add(old, new):
    assert old not in M, f"dup old {old}"
    M[old] = new

# ---- root ----
add("PostServiceApplication.java", "PostServiceApplication.java")

# ---- exception -> shared/exception ----
for f in ["AppException", "GlobalExceptionHandler", "StatusCode"]:
    add(f"exception/{f}.java", f"shared/exception/{f}.java")

# ---- config ----
SHARED_CONFIG = ["AppConfig", "ApplicationAuditAware", "AuditingConfig", "CorsConfig",
                  "OpenAPIConfig", "PageConfig", "RabbitMQConfig", "RedisConfig",
                  "RestTemplateConfig", "WebSocketConfig"]
for f in SHARED_CONFIG:
    add(f"config/{f}.java", f"shared/config/{f}.java")
SHARED_SECURITY = ["CustomJwtDecode", "JwtAuthenticationEntryPoint", "JwtConfig",
                    "StompAuthChannelInterceptor"]
for f in SHARED_SECURITY:
    add(f"config/{f}.java", f"shared/security/{f}.java")
add("config/AIChatConfig.java", "ai/internal/config/AIChatConfig.java")
add("config/ConfigCloudinary.java", "media/internal/config/ConfigCloudinary.java")
add("config/DefaultMediaConfig.java", "user/internal/config/DefaultMediaConfig.java")
add("config/FirebaseConfig.java", "notification/internal/config/FirebaseConfig.java")

# ---- persistence / mail / util -> shared ----
add("entity/AbstractEntity.java", "shared/persistence/AbstractEntity.java")
add("util/UuidV7Generator.java", "shared/persistence/UuidV7Generator.java")
add("util/DateUtils.java", "shared/util/DateUtils.java")
add("util/MailUtils.java", "shared/mail/MailUtils.java")
add("dto/response/MailInformation.java", "shared/mail/MailInformation.java")

# ---- response / cursor -> shared ----
add("dto/ApiResponse.java", "shared/response/ApiResponse.java")
add("dto/ApiDataCursor.java", "shared/response/ApiDataCursor.java")
add("dto/response/SearchPageResponse.java", "shared/response/SearchPageResponse.java")

# ---- redis -> shared ----
add("services/RedisService.java", "shared/redis/RedisService.java")
add("services/impl/RedisServiceImpl.java", "shared/redis/RedisServiceImpl.java")
add("services/impl/RedisTestService.java", "shared/redis/RedisTestService.java")

# ---- messaging (dead code moved as-is) -> shared ----
add("services/KafkaService.java", "shared/messaging/KafkaService.java")
add("enums/TopicKafka.java", "shared/messaging/TopicKafka.java")

# ---- shared exception enums ----
for f in ["AccountErrorCode", "AccountResponseStatus", "AccountValidErrorCode", "CodeEnum"]:
    add(f"enums/{f}.java", f"shared/exception/{f}.java")
add("mapper".replace("mapper", "enums") + "/RedisKeyType.java".replace("/RedisKeyType.java", "") if False else "enums/RedisKeyType.java", "shared/redis/RedisKeyType.java")
add("enums/MailTemplate.java", "shared/mail/MailTemplate.java")
add("enums/AMQPConst.java", "shared/messaging/AMQPConst.java")
add("enums/ResponseStatus.java", "shared/response/ResponseStatus.java")

# ---- shared health ----
add("controller/CheckConnection.java", "shared/health/CheckConnection.java")

# ===================== USER (+ RBAC) =====================
for f in ["Account", "Follow"]:
    add(f"entity/{f}.java", f"user/internal/entity/{f}.java")
for f in ["AccountRepository", "FollowRepository"]:
    add(f"repository/{f}.java", f"user/internal/repository/{f}.java")
for f in ["AccountService", "ProfileService"]:
    add(f"services/{f}.java", f"user/internal/service/{f}.java")
for f in ["AccountServiceImpl", "ProfileServiceImpl"]:
    add(f"services/impl/{f}.java", f"user/internal/service/{f}.java")
for f in ["AccountController", "ProfileController", "InternalAccountController"]:
    add(f"controller/{f}.java", f"user/internal/controller/{f}.java")
for f in ["AccountMapper", "ProfileMapper"]:
    add(f"mapper/{f}.java", f"user/internal/mapper/{f}.java")
add("util/DisplayNameUtils.java", "user/internal/util/DisplayNameUtils.java")
add("util/DefaultMediaProvider.java", "user/internal/config/DefaultMediaProvider.java")
add("repository/httpClient/ProfileClient.java", "user/internal/client/ProfileClient.java")
for f in ["ProfileDTO", "ProfileResponse", "AccountResponse", "AccountStatisticRegisterDTO",
          "AccountStatisticRegisterLongDateDTO", "DuplicationResponse", "ManageUserResponse",
          "AccountRegisterRequest", "ChangePasswordRequest", "DuplicationRequest",
          "ProfileRegisterRequest", "UpdateProfileRequest"]:
    src = {
        "ProfileDTO": "dto/profile/ProfileDTO.java", "ProfileResponse": "dto/profile/ProfileResponse.java",
        "AccountResponse": "dto/response/AccountResponse.java",
        "AccountStatisticRegisterDTO": "dto/response/AccountStatisticRegisterDTO.java",
        "AccountStatisticRegisterLongDateDTO": "dto/response/AccountStatisticRegisterLongDateDTO.java",
        "DuplicationResponse": "dto/response/DuplicationResponse.java",
        "ManageUserResponse": "dto/response/ManageUserResponse.java",
        "AccountRegisterRequest": "dto/request/AccountRegisterRequest.java",
        "ChangePasswordRequest": "dto/request/ChangePasswordRequest.java",
        "DuplicationRequest": "dto/request/DuplicationRequest.java",
        "ProfileRegisterRequest": "dto/request/ProfileRegisterRequest.java",
        "UpdateProfileRequest": "dto/request/UpdateProfileRequest.java",
    }[f]
    add(src, f"user/internal/dto/{f}.java")
add("dto/relationships/FollowRequest.java", "user/internal/dto/FollowRequest.java")
add("dto/ActorSnapshotDTO.java", "user/api/ActorSnapshotDTO.java")

# RBAC -> user/internal/rbac (flat)
for f in ["Role", "Permission"]:
    add(f"entity/{f}.java", f"user/internal/rbac/{f}.java")
for f in ["RoleRepository", "PermissionRepository"]:
    add(f"repository/{f}.java", f"user/internal/rbac/{f}.java")
for f in ["RoleService", "PermissionService"]:
    add(f"services/{f}.java", f"user/internal/rbac/{f}.java")
for f in ["RoleServiceImpl", "PermissionServiceImpl"]:
    add(f"services/impl/{f}.java", f"user/internal/rbac/{f}.java")
add("seed/RoleSedd.java", "user/internal/rbac/RoleSedd.java")
for f in ["RoleController", "PermissionController"]:
    add(f"controller/{f}.java", f"user/internal/rbac/{f}.java")
for f in ["RoleMapper", "PermissionMapper"]:
    add(f"mapper/{f}.java", f"user/internal/rbac/{f}.java")
add("dto/request/RoleCreationRequest.java", "user/internal/rbac/RoleCreationRequest.java")
add("dto/request/PermissionRequest.java", "user/internal/rbac/PermissionRequest.java")
add("dto/response/RoleResponse.java", "user/internal/rbac/RoleResponse.java")
add("dto/response/PermissionResponse.java", "user/internal/rbac/PermissionResponse.java")

# ===================== AUTH =====================
for f in ["Token", "RefreshToken", "AuthProviderCredential"]:
    add(f"entity/{f}.java", f"auth/internal/entity/{f}.java")
for f in ["TokenRepository", "RefreshTokenRepository", "AuthProviderRepository"]:
    add(f"repository/{f}.java", f"auth/internal/repository/{f}.java")
for f in ["AuthenticationService", "JwtService", "RefreshTokenService", "OtpService",
          "BanService", "GoogleOAuthService"]:
    add(f"services/{f}.java", f"auth/internal/service/{f}.java")
for old, new in [("AuthenticationServiceImpl", "AuthenticationServiceImpl"),
                  ("JwtServiceImpl", "JwtServiceImpl"),
                  ("RefreshTokenServiceImpl", "RefreshTokenServiceImpl"),
                  ("OtpServiceImpl", "OtpServiceImpl"),
                  ("BanServiceImpl", "BanServiceImpl"),
                  ("GoogleOAuthSerivceImpl", "GoogleOAuthSerivceImpl")]:
    add(f"services/impl/{old}.java", f"auth/internal/service/{new}.java")
add("controller/AuthenticateController.java", "auth/internal/controller/AuthenticateController.java")
add("enums/AuthProvider.java", "auth/api/AuthProvider.java")
for f in ["AccountLoginRequest", "EmailRequest", "OtpRequest", "RefreshTokenRequest",
          "ResetPasswordRequest", "TokenRequest"]:
    add(f"dto/request/{f}.java", f"auth/internal/dto/{f}.java")
for f in ["AuthenticationResponse", "IntrospectResponse"]:
    add(f"dto/response/{f}.java", f"auth/internal/dto/{f}.java")
for f in ["GoogleAuthResponse", "GoogleDTORequest", "GoogleUserInfo"]:
    add(f"dto/google/{f}.java", f"auth/internal/dto/{f}.java")

# ===================== POST (+ feed) =====================
for f in ["Post", "PostLike"]:
    add(f"entity/{f}.java", f"post/internal/entity/{f}.java")
for f in ["PostRepository", "PostLikeRepository"]:
    add(f"repository/{f}.java", f"post/internal/repository/{f}.java")
add("services/PostService.java", "post/internal/service/PostService.java")
add("services/impl/PostServiceImpl.java", "post/internal/service/PostServiceImpl.java")
add("controller/PostController.java", "post/internal/controller/PostController.java")
add("util/PostUtils.java", "post/internal/util/PostUtils.java")
add("mapper/PostMapper.java", "post/internal/mapper/PostMapper.java")
add("publisher/PostEventPublisher.java", "post/internal/publisher/PostEventPublisher.java")
for f in ["ContentResponse", "LikePostDTO", "MediaResponse", "PostDTO", "PostDTORequest",
          "PostOriginResponse", "PostResponse", "PostShareDTORequest", "PostStatisticsDTO",
          "PostStatisticsLongDateDTO"]:
    add(f"dto/post/{f}.java", f"post/internal/dto/{f}.java")
add("dto/ContentDTO.java", "post/api/ContentDTO.java")
add("dto/ContentDTORequest.java", "post/api/ContentDTORequest.java")

# feed subpackage
for f in ["SeenPost", "UserInterest", "Hashtag"]:
    add(f"entity/{f}.java", f"post/internal/feed/entity/{f}.java")
for f in ["SeenPostRepository", "UserInterestRepository", "HashtagRepository"]:
    add(f"repository/{f}.java", f"post/internal/feed/repository/{f}.java")
for f in ["FeedService", "ScoringService", "InterestGraphService"]:
    add(f"services/{f}.java", f"post/internal/feed/service/{f}.java")
for f in ["FeedServiceImpl", "ScoringServiceImpl", "InterestGraphServiceImpl"]:
    add(f"services/impl/{f}.java", f"post/internal/feed/service/{f}.java")
for f in ["HashtagSyncScheduler", "InterestDecayScheduler", "SeenPostPurgeScheduler"]:
    add(f"scheduler/{f}.java", f"post/internal/feed/scheduler/{f}.java")
for f in ["ScoreUpdateConsumer", "InterestUpdateConsumer"]:
    add(f"comsumer/{f}.java", f"post/internal/feed/consumer/{f}.java")
add("publisher/InteractionEventPublisher.java", "post/internal/feed/publisher/InteractionEventPublisher.java")
add("dto/feed/InteractionEvent.java", "post/internal/feed/dto/InteractionEvent.java")

# ===================== COMMENT =====================
for f in ["Comment", "CommentLike", "CommentMedia"]:
    add(f"entity/{f}.java", f"comment/internal/entity/{f}.java")
for f in ["CommentRepository", "CommentLikeRepository"]:
    add(f"repository/{f}.java", f"comment/internal/repository/{f}.java")
for f in ["CommentService", "ReplyCommentService"]:
    add(f"services/{f}.java", f"comment/internal/service/{f}.java")
for f in ["CommentServiceImpl", "ReplyCommentServiceImpl"]:
    add(f"services/impl/{f}.java", f"comment/internal/service/{f}.java")
for f in ["CommentController", "ReplyCommentController"]:
    add(f"controller/{f}.java", f"comment/internal/controller/{f}.java")
add("comsumer/CommentConsumer.java", "comment/internal/consumer/CommentConsumer.java")
for f in ["CommentDTO", "CommentDTORequest", "CommentResponse", "CommentUpdateDTORequest", "LikeCommentDTO"]:
    add(f"dto/comment/{f}.java", f"comment/internal/dto/{f}.java")
for f in ["LikeReplyCommentDTO", "ReplyCommentRequest", "ReplyCommentResponse", "ReplyCommentUpdateDTORequest"]:
    add(f"dto/replyComment/{f}.java", f"comment/internal/dto/{f}.java")

# ===================== MEDIA =====================
add("entity/Attachments.java", "media/internal/entity/Attachments.java")
add("repository/AttachmentsRepository.java", "media/internal/repository/AttachmentsRepository.java")
for f in ["AttachmentsService", "UploadMedia"]:
    add(f"services/{f}.java", f"media/internal/service/{f}.java")
for f in ["AttachmentsServiceImpl", "UploadMediaImpl"]:
    add(f"services/impl/{f}.java", f"media/internal/service/{f}.java")
add("mapper/AttachmentMapper.java", "media/internal/mapper/AttachmentMapper.java")
add("util/MediaUploadUtils.java", "media/internal/util/MediaUploadUtils.java")
for f in ["AttachmentsController", "FileUploadController", "InternalApi"]:
    add(f"controller/{f}.java", f"media/internal/controller/{f}.java")
add("comsumer/AttachmentsConsumer.java", "media/internal/consumer/AttachmentsConsumer.java")
add("dto/Attachments/AttachementProfileDTO.java", "media/internal/dto/AttachementProfileDTO.java")
add("dto/Attachments/AttachmentDTO.java", "media/internal/dto/AttachmentDTO.java")
add("dto/page/AttachmentsRequest.java", "media/internal/dto/AttachmentsRequest.java")
add("dto/post/MediaItemDTO.java", "media/api/MediaItemDTO.java")
for f in ["AttachmentType", "MediaType", "MediaLayoutType"]:
    add(f"enums/{f}.java", f"media/api/{f}.java")

# ===================== CHAT =====================
for f in ["Conversation", "ConversationMember", "Message", "MessageAttachment"]:
    add(f"entity/{f}.java", f"chat/internal/entity/{f}.java")
for f in ["ConversationRepository", "ConversationMemberRepository", "MessageRepository"]:
    add(f"repository/{f}.java", f"chat/internal/repository/{f}.java")
add("services/ChatService.java", "chat/internal/service/ChatService.java")
for f in ["ChatServiceImpl", "OnlineServiceImpl"]:
    add(f"services/impl/{f}.java", f"chat/internal/service/{f}.java")
add("mapper/ChatMapper.java", "chat/internal/mapper/ChatMapper.java")
for f in ["ConversationController", "ChatWsController"]:
    add(f"controller/{f}.java", f"chat/internal/controller/{f}.java")
for f in ["ConversationDTO", "CreateConversationRequest", "MarkReadDTO", "MessageDTO", "SendMessageRequest"]:
    add(f"dto/message/{f}.java", f"chat/internal/dto/{f}.java")
add("dto/conversation/ConversationMemberDTO.java", "chat/internal/dto/ConversationMemberDTO.java")
for f in ["MemberRole", "ConversationType", "MessageAttachmentType", "MessageType"]:
    add(f"enums/{f}.java", f"chat/internal/enums/{f}.java")

# ===================== NOTIFICATION =====================
for f in ["Notification", "DeviceToken", "NotificationPreference", "NotificationTemplate",
          "ChannelSettings", "EmailTemplate", "EmailTemplateField", "LocalizedTemplate"]:
    add(f"entity/{f}.java", f"notification/internal/entity/{f}.java")
for f in ["NotificationRepository", "DeviceTokenRepository", "NotificationPreferenceRepository",
          "NotificationTemplateRepository"]:
    add(f"repository/{f}.java", f"notification/internal/repository/{f}.java")
add("services/NotificaitonService.java", "notification/internal/service/NotificaitonService.java")
add("services/DeviceTokenService.java", "notification/internal/service/DeviceTokenService.java")
add("services/DemoNotificationService.java", "notification/internal/service/DemoNotificationService.java")
for f in ["NotificationServiceImpl", "DeviceTokenServiceImpl", "DemoNotificationServiceImpl", "FcmServiceImpl"]:
    add(f"services/impl/{f}.java", f"notification/internal/service/{f}.java")
add("controller/NotificationController.java", "notification/internal/controller/NotificationController.java")
add("comsumer/NotificationConsumer.java", "notification/internal/consumer/NotificationConsumer.java")
add("scheduler/AppointmentReminderScheduler.java", "notification/internal/scheduler/AppointmentReminderScheduler.java")
add("publisher/NotificationEvent.java", "notification/api/NotificationEvent.java")
add("enums/NotificationType.java", "notification/api/NotificationType.java")
for f in ["DemoNotificationRequest", "DeviceTokenDTO", "NoticeRequest", "NotifiDemoDTO",
          "NotificationCursorResponse", "NotificationDTO", "NotificationGroupResponse", "NotificationResponse"]:
    add(f"dto/notification/{f}.java", f"notification/internal/dto/{f}.java")
add("dto/TypeNotification.java", "notification/internal/dto/TypeNotification.java")
for f in ["ChannelType", "DeviceType", "PaymentStatus", "NotificationConst", "NotifyTo"]:
    add(f"enums/{f}.java", f"notification/internal/enums/{f}.java")

# ===================== COMPLAINT =====================
for f in ["Complaint", "ComplaintDetail"]:
    add(f"entity/{f}.java", f"complaint/internal/entity/{f}.java")
add("repository/ComplaintRepository.java", "complaint/internal/repository/ComplaintRepository.java")
add("services/ComplaintService.java", "complaint/internal/service/ComplaintService.java")
for f in ["ComplainServiceImpl", "ComplaintServiceImpl"]:
    add(f"services/impl/{f}.java", f"complaint/internal/service/{f}.java")
add("mapper/ComplantMapper.java", "complaint/internal/mapper/ComplantMapper.java")
add("controller/ComplaintController.java", "complaint/internal/controller/ComplaintController.java")
for f in ["ComplaintDTO", "ComplaintDTOResponse", "ComplaintStatisticsDTO", "ComplaintStatisticsLongDayDTO"]:
    add(f"dto/complaint/{f}.java", f"complaint/internal/dto/{f}.java")
add("enums/ComplaintType.java", "complaint/internal/enums/ComplaintType.java")

# ===================== TERM OF SERVICE =====================
add("entity/TermOfServices.java", "termofservice/internal/entity/TermOfServices.java")
for f in ["TermRepository", "TermOfServicesRepository"]:
    add(f"repository/{f}.java", f"termofservice/internal/repository/{f}.java")
add("services/TermOfServicesService.java", "termofservice/internal/service/TermOfServicesService.java")
add("services/impl/TermOfServicesServiceImpl.java", "termofservice/internal/service/TermOfServicesServiceImpl.java")
add("mapper/TermOfServiceMapper.java", "termofservice/internal/mapper/TermOfServiceMapper.java")
add("controller/TermOfServiceController.java", "termofservice/internal/controller/TermOfServiceController.java")
add("dto/termOfService/TermOfServiceDTO.java", "termofservice/internal/dto/TermOfServiceDTO.java")

# ===================== FAQ =====================
for f in ["Faq", "FaqRating", "FaqView"]:
    add(f"entity/{f}.java", f"faq/internal/entity/{f}.java")
for f in ["FaqRepository", "FaqRatingRepository", "FaqViewRepository"]:
    add(f"repository/{f}.java", f"faq/internal/repository/{f}.java")
add("services/FaqService.java", "faq/internal/service/FaqService.java")
add("services/impl/FaqServiceImpl.java", "faq/internal/service/FaqServiceImpl.java")
add("mapper/FaqMapper.java", "faq/internal/mapper/FaqMapper.java")
add("controller/FaqController.java", "faq/internal/controller/FaqController.java")
for f in ["FaqDTO", "FaqRatingRequest", "FaqRatingStatsDTO", "FaqRequest"]:
    add(f"dto/faq/{f}.java", f"faq/internal/dto/{f}.java")
add("enums/FaqType.java", "faq/internal/enums/FaqType.java")

# ===================== AI =====================
add("services/AIChatService.java", "ai/internal/service/AIChatService.java")
add("services/impl/AIChatServiceImpl.java", "ai/internal/service/AIChatServiceImpl.java")
for f in ["AIChatController", "AIChatWsController"]:
    add(f"controller/{f}.java", f"ai/internal/controller/{f}.java")
for f in ["AiChatReply", "AiChatRequest"]:
    add(f"dto/ai/{f}.java", f"ai/internal/dto/{f}.java")

# ---------------------------------------------------------------------------
# Test files
# ---------------------------------------------------------------------------
TM = {
    "FeedServiceBudgetAllocationTest.java": "post/internal/feed/FeedServiceBudgetAllocationTest.java",
    "InterestDecaySchedulerTest.java": "post/internal/feed/InterestDecaySchedulerTest.java",
    "InterestGraphServiceTest.java": "post/internal/feed/InterestGraphServiceTest.java",
    "ScoringServiceTest.java": "post/internal/feed/ScoringServiceTest.java",
    "ReplyCommentServiceImplTest.java": "comment/internal/service/ReplyCommentServiceImplTest.java",
    "MigrationSanityTest.java": "MigrationSanityTest.java",
    "PostServiceApplicationTests.java": "PostServiceApplicationTests.java",
}

# ---------------------------------------------------------------------------
# Transformation logic (Phase A: pure mechanical move, no behavior change)
# ---------------------------------------------------------------------------

def pkg_of(rel):
    d = os.path.dirname(rel)
    return "com.fsocial" + ("." + d.replace("/", ".") if d else "")

def simple_name_of(rel):
    return os.path.basename(rel)[:-5]  # strip ".java"

# old FQCN -> new FQCN, for every moved main-source class
FQCN_MAP = {}
for _old, _new in M.items():
    FQCN_MAP[pkg_of(_old) + "." + simple_name_of(_old)] = pkg_of(_new) + "." + simple_name_of(_new)

# old package (dotted) -> [(simple_name, new_fqcn), ...]  (main files)
OLD_PACKAGE_MEMBERS = {}
for _old, _new in M.items():
    OLD_PACKAGE_MEMBERS.setdefault(pkg_of(_old), []).append(
        (simple_name_of(_old), pkg_of(_new) + "." + simple_name_of(_new))
    )

# same, for the 7 test files (all originally sat directly in com.fsocial)
TEST_OLD_PACKAGE_MEMBERS = {}
for _old, _new in TM.items():
    TEST_OLD_PACKAGE_MEMBERS.setdefault(pkg_of(_old), []).append(
        (simple_name_of(_old), pkg_of(_new) + "." + simple_name_of(_new))
    )

_FQCN_SORTED = sorted(FQCN_MAP.keys(), key=len, reverse=True)
_FQCN_PATTERN = re.compile(r"\b(" + "|".join(re.escape(k) for k in _FQCN_SORTED) + r")\b")

def replace_fqcns(text):
    """Word-boundary-safe literal replace of every moved class's old FQCN with its
    new FQCN, across the WHOLE file text. Covers plain imports, static imports,
    and FQCN string literals inside @Query(...) / MapStruct expression="java(...)"."""
    return _FQCN_PATTERN.sub(lambda m: FQCN_MAP[m.group(1)], text)

_WILDCARD_RE = re.compile(r'^([ \t]*)import(\s+static)?\s+([\w.]+)\.\*;[ \t]*$', re.MULTILINE)

def resolve_wildcards(text, pkg_members_index):
    """Rewrite `import OLDPKG.*;` to the single new package wildcard when every
    member of OLDPKG moved to the same new package; otherwise delete the line
    (those references get resolved by synth_bare_imports instead)."""
    used_pkgs = []
    def _sub(m):
        indent, is_static, pkg = m.group(1), m.group(2), m.group(3)
        members = pkg_members_index.get(pkg)
        if not members:
            return m.group(0)
        used_pkgs.append(pkg)
        new_pkgs = sorted(set(nf.rsplit(".", 1)[0] for _, nf in members))
        if len(new_pkgs) == 1:
            prefix = "import static " if is_static else "import "
            return f"{indent}{prefix}{new_pkgs[0]}.*;"
        return ""
    new_text = _WILDCARD_RE.sub(_sub, text)
    return new_text, used_pkgs

_IMPORT_LINE_RE = re.compile(r'^import\s+(?:static\s+)?([\w.]+);[ \t]*$', re.MULTILINE)

def existing_imported_simple_names(text):
    names = set()
    for m in _IMPORT_LINE_RE.finditer(text):
        fq = m.group(1)
        if fq.endswith(".*"):
            continue
        names.add(fq.rsplit(".", 1)[-1])
    return names

def synth_bare_imports(text, own_simple_name, own_new_pkg, candidate_old_pkgs, pkg_members_index):
    """For classes formerly reachable in this file WITHOUT an explicit import
    (same-old-package bare reference, or via a now-removed multi-target wildcard),
    add an explicit import to the new FQCN if the simple name is actually used
    and the class now lives in a different new package."""
    already = existing_imported_simple_names(text)
    to_add = []
    seen = set()
    for op in candidate_old_pkgs:
        for sn, nf in pkg_members_index.get(op, []):
            if sn == own_simple_name or sn in already or sn in seen:
                continue
            if nf.rsplit(".", 1)[0] == own_new_pkg:
                continue
            if re.search(r'\b' + re.escape(sn) + r'\b', text):
                to_add.append(nf)
                seen.add(sn)
    if not to_add:
        return text
    lines = text.split("\n")
    insert_at = None
    for i, line in enumerate(lines):
        if line.strip().startswith("import "):
            insert_at = i + 1
    if insert_at is None:
        for i, line in enumerate(lines):
            if line.strip().startswith("package "):
                insert_at = i + 1
                break
    if insert_at is None:
        insert_at = 0
    new_import_lines = [f"import {fq};" for fq in sorted(to_add)]
    lines[insert_at:insert_at] = new_import_lines
    return "\n".join(lines)

def transform_file(old_rel, new_rel, content, pkg_members_index):
    old_pkg = pkg_of(old_rel)
    new_pkg = pkg_of(new_rel)
    own_simple = simple_name_of(old_rel)

    content = re.sub(
        r'^package\s+' + re.escape(old_pkg) + r'\s*;',
        f'package {new_pkg};',
        content, count=1, flags=re.MULTILINE,
    )
    content = replace_fqcns(content)
    content, wildcard_pkgs_used = resolve_wildcards(content, pkg_members_index)
    candidate_pkgs = set(wildcard_pkgs_used)
    candidate_pkgs.add(old_pkg)
    content = synth_bare_imports(content, own_simple, new_pkg, candidate_pkgs, pkg_members_index)
    return content

def run_migration():
    for old_rel, new_rel in M.items():
        old_path = os.path.join(MAIN, old_rel)
        new_path = os.path.join(MAIN, new_rel)
        with open(old_path, "r", encoding="utf-8") as f:
            content = f.read()
        content = transform_file(old_rel, new_rel, content, OLD_PACKAGE_MEMBERS)
        os.makedirs(os.path.dirname(new_path), exist_ok=True)
        with open(new_path, "w", encoding="utf-8") as f:
            f.write(content)
        if os.path.abspath(old_path) != os.path.abspath(new_path):
            os.remove(old_path)

    # test files can reference both moved production classes and each other
    test_pkg_members_index = {k: list(v) for k, v in OLD_PACKAGE_MEMBERS.items()}
    for k, v in TEST_OLD_PACKAGE_MEMBERS.items():
        test_pkg_members_index.setdefault(k, [])
        test_pkg_members_index[k] = test_pkg_members_index[k] + v

    for old_rel, new_rel in TM.items():
        old_path = os.path.join(TEST, old_rel)
        new_path = os.path.join(TEST, new_rel)
        with open(old_path, "r", encoding="utf-8") as f:
            content = f.read()
        content = transform_file(old_rel, new_rel, content, test_pkg_members_index)
        os.makedirs(os.path.dirname(new_path), exist_ok=True)
        with open(new_path, "w", encoding="utf-8") as f:
            f.write(content)
        if os.path.abspath(old_path) != os.path.abspath(new_path):
            os.remove(old_path)

    # remove now-empty old directories
    for root_dir in (MAIN, TEST):
        for dirpath, _dirnames, _filenames in os.walk(root_dir, topdown=False):
            if dirpath == root_dir:
                continue
            if not os.listdir(dirpath):
                os.rmdir(dirpath)

if __name__ == "__main__":
    print(f"Total main mappings: {len(M)}")
    print(f"Total test mappings: {len(TM)}")
    if "--apply" in sys.argv:
        run_migration()
        print("Migration applied.")
    else:
        print("Dry run only (pass --apply to execute).")
