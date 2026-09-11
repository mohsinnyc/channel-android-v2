# Channel Service — Backend API Reference

Context document for `channel-android-v2`. Generated from the actual `channel-service`
controllers/DTOs/services (not from memory or the old v1 app) so it can be trusted as
ground truth when writing Android networking code. Re-generate/update this file whenever
the backend's API surface changes — treat drift here the same as a compile error.

**Base URL / prefix:** no global prefix (`main.ts` never calls `setGlobalPrefix`). Every
path below is exactly `@Controller()` path + method path, served at the app root — e.g.
`POST /auth/login`, `GET /feed`.

**Global validation pipe:** `ValidationPipe({ whitelist: true, forbidNonWhitelisted: true, transform: true, stopAtFirstError: false })`
— unknown body fields are stripped then the whole request is rejected (400) if any were
present; every field is coerced to its declared type; **all** validation errors for a
request are collected, not just the first one.

## Global guards / interceptors / filters

Applied to every route, in this order:

1. **`ThrottlerGuard`** — default global limit **120 requests / 60s per client IP**,
   overridden per-route by `@Throttle(...)` (noted per-endpoint below). Over the limit → 429.
2. **`JwtAuthGuard`** — reads `Authorization: Bearer <token>`, verifies as an **access**
   token (`type:'access'`, signed with `JWT_ACCESS_SECRET`), loads the user, rejects if
   missing/banned/deleted. Sets `request.user = {userId, username}`. Skipped for `@Public()`
   routes. **Always throws 401** on any auth failure (never 403) — deliberately, so a client
   knows "try refreshing the token" is always the right response to 401.
3. **`VerifiedEmailGuard`** — runs after the JWT guard, skipped for `@Public()` and
   `@AllowUnverified()` routes. For every other route, an authenticated-but-unverified user
   gets **403 "Verify your email to continue"**. Most of the API requires a *verified*
   email, not just a valid token.
4. **`ApiExceptionFilter`** — normalizes every thrown exception into
   `{ statusCode, errorCode, message, details?, path, requestId, timestamp }`. For 5xx, the
   client-facing `message` is always the generic `"An unexpected error occurred"` (the real
   error only goes to server logs/Sentry); for 4xx the real message(s) pass through, with
   `details` populated when there's more than one validation error. `X-Request-Id` header
   and `requestId` field are always present.
5. **`MediaUrlInterceptor`** (registered globally) — after every controller returns, this
   deep-walks the **entire** response body and, for any object key named exactly
   `profileImageUrl`, `audioBioUrl`, `profileAudioUrl`, `imageUrl`, or `audioUrl` holding a
   non-empty string, replaces it with a **freshly S3-presigned GET URL**
   (TTL = `MEDIA_READ_URL_TTL_SECONDS`, default 3600s). **Every media URL returned by any
   endpoint is short-lived — never cache/persist one as a stable identifier.** Already-signed
   URLs pass through unchanged (detected via `X-Amz-Signature`/`X-Amz-Credential`).

## Custom decorators

- **`@Public()`** — bypasses both `JwtAuthGuard` and `VerifiedEmailGuard`; no token required at all.
- **`@AllowUnverified()`** — still requires a valid bearer token, but skips the email-verification gate.
- **`@Throttle({ default: { limit, ttl } })`** — per-route rate-limit override (`ttl` in ms). Listed per-endpoint below.
- **`AdminGuard`** — requires `request.user` to resolve to `isAdmin === true` (not banned/deleted). Runs *in addition to* the global guards — admin routes still need a valid, verified bearer token first.
- **`@CurrentUser()`** — param decorator injecting `{ userId, username }` from `request.user`.

## Shared enums

```
OnboardingState: "New_User" | "Profile_Image" | "Profile_Bio" | "Profile_Audio" | "Profile_Interests" | "OnBoarding_Complete"
PostType:        "POST" | "REPOST" | "QUOTE"
PostStatus:      "AVAILABLE" | "MODERATED" | "DELETED"
```
New users start at `onboardingState = "Profile_Image"` (set explicitly in `AuthService.signUp`).

Other string-typed constrained fields (not TS enums, but fixed value sets):
- Report `targetType`: `"USER" | "POST" | "COMMENT" | "REPLY"`
- Report `status`: `"OPEN" | "RESOLVED" | "DISMISSED"` (default `OPEN`)
- Moderation `action`: `"BAN" | "UNBAN" | "REMOVE" | "RESTORE"`
- Notification `type`: `"CHANNEL_VIEWS" | "REQUEST_ACCEPTED" | "POST_REPLY" | "GENERIC"` — any other stored value collapses to `GENERIC` on read.
- Upload `mediaType`: `"image" | "audio"`; `purpose`: `"profile" | "post" | "temp" | "comment"`; upload row `status`: `"PENDING" | "READY"`.
- Device token `platform`: stored uppercased, `"ANDROID" | "IOS"`.

## Shared response object shapes

### `PostObject`

The shape returned by every endpoint that returns posts (`GET /posts/:id`, `GET /feed`,
`GET /explore/*`, `GET /profile*`, `GET /search`, etc.), produced by
`PostReadService.hydrate()`. Every field is present on every post object (nulls are real,
not omitted), except `referencedPost`/`quotedPost` which only matter for `REPOST`/`QUOTE`:

```
postId: string
userId: string | null              // null if post/author unavailable
username: string | null
isVerified: boolean | null
profileImageUrl: string | null     // signed
content: string | null
imageUrl: string | null            // signed
audioUrl: string | null            // signed
audioDurationMs: number | null
audioPlayCount: number | null
mentions: { userId, username, startIndex, endIndex }[] | null
createdAt: string | null           // ISO 8601
postType: PostType | null
postStatus: PostStatus             // effective status seen BY THIS VIEWER - see note below
category: { id, label, sortOrder, isActive } | null
referencedPost: PostObject | null  // present only when postType !== "POST"; itself fully hydrated + carries `quotedPost`
isLiked: boolean
isRepostedByUser: boolean
isOwnPost: boolean
likeCount: number
commentCount: number
repostCount: number
```
A referenced/embedded post additionally carries:
```
quotedPost: PostObject | null   // only set when THAT post is itself a QUOTE; never unwrapped further than one level
```

**"Unavailable" masking:** a post is unavailable to the viewer if its status isn't
`AVAILABLE`, or its author is banned/blocked-either-way/private-and-not-followed-and-not-self.
When unavailable: `userId, username, isVerified, profileImageUrl, content, imageUrl,
audioUrl, audioDurationMs, audioPlayCount, mentions, createdAt, category` all become `null`,
and `postStatus` is forced to `MODERATED` if it would otherwise still read `AVAILABLE`.
**A client cannot distinguish "admin-moderated" from "hidden due to block/privacy" from
`postStatus` alone.**

**Repost unwrapping:** if a `REPOST`'s source is itself a repost, the reader unwraps to the
ultimate non-repost source before hydrating `referencedPost` — `referencedPost` never itself
has `postType: "REPOST"`.

### `ProfileObject` (`GET /profile`, `GET /profile/:userId`)

```
userId: string
username: string
isVerified: boolean
bio: string | null
bioMentions: { userId, username, startIndex, endIndex }[] | null
profileImageUrl: string | null   // signed
audioBioUrl: string              // NOTE: always a string, defaults to '' (never null) - signed when non-empty
audioBioPlayCount: number
listenerCount: number             // follower count
listeningCount: number            // following count
pendingRequestsCount: number      // only meaningful when viewer === target; else 0
isFollowing: boolean              // false when viewer === target
isPrivate: boolean
isFollowRequested: boolean        // false when viewer === target
posts: PostObject[]               // empty [] if viewer can't see this user's posts
nextPageKey: string | null        // cursor for `posts`
```
404s ("User not found") if the target doesn't exist, is banned, or blocks/is blocked by the viewer.

### `RelationshipUser` (`GET /user/blocked`, `GET /user/:userId/listeners`, `GET /user/:userId/listening`)
```
{ userId, username, isVerified, profileImageUrl, audioBioUrl, isFollowing: boolean }
```

### `SearchUser` (`GET /users/search`, and the `users[]` inside `GET /search`)
```
{ userId, username, displayName: string | null, profileImageUrl, profileAudioUrl, isVerified, isFollowing }
```
Note: this endpoint's audio field is named `profileAudioUrl` (still an alias for `audioBioUrl`, and still in the signable-field set).

### `NotificationItem` (`GET /notifications`)
```
{
  id: string,
  type: "CHANNEL_VIEWS" | "REQUEST_ACCEPTED" | "POST_REPLY" | "GENERIC",
  title: string,
  body: string | null,
  actor: { userId, username, profileImageUrl, isVerified } | null,
  target: { userId?, postId?, commentId? } | null,
  count: number | null,
  isRead: boolean,
  createdAtEpochMs: number,     // epoch millis, NOT an ISO string
  metadata: Record<string, string>
}
```

### `CommentObject` / `ReplyObject`
```
// Comment
{
  commentId, postId,
  author: { userId, username, profileImageUrl },
  content: string | null,        // note: field is `content`; entity column is `text`
  isDeleted: boolean,
  audioUrl: string | null, audioPlayCount: number, imageUrl: string | null,
  createdAt: string (ISO),
  likeCount: number, isLikedByUser: boolean,
  replyCount: number,
  mentions: [...] | null
}
// Reply - identical shape, keyed `replyId, commentId` instead, and no `replyCount`.
```
`DELETE /comments/:id` / `DELETE /reply/:id` **hard-delete** (row disappears from listings
entirely). `isDeleted: true` only ever comes from admin moderation (soft-delete: text/media
nulled, row kept and still returned in listings).

### Pagination / cursors

Two schemes:
- **`encodeCursor(createdAt, id)`** — base64url of `{createdAt, id}`. Used by posts
  (`nextPageKey`), comments/replies (`nextCursor`), blocked/listeners/listening
  (`nextPageKey`). Descending keyset pagination. An invalid cursor → **400 "Invalid
  pagination cursor"**.
- **Notification cursor** — separately implemented: base64url of just the ISO date string,
  single-field comparison (no id tiebreak).
- Replies are the only listing returned **ascending** (oldest-first); everything else
  (posts, comments, notifications, follow lists) is newest-first.
- Most list endpoints silently **clamp** an out-of-range `limit` into range rather than
  rejecting it — you can't get a 400 by asking for too many.

---

## Auth (`/auth`)

| Method & Path | Auth | Throttle | Notes |
|---|---|---|---|
| `GET /auth/status` | Bearer + `@AllowUnverified()` | — | Returns `{username, email, isEmailVerified, isBanned, onboardingState}`. |
| `POST /auth/signup` | `@Public()` | 10/60s | Body `SignUpDto`. Returns `AuthResponse`. |
| `POST /auth/login` | `@Public()` | 10/60s | Body `LoginDto`. Returns `AuthResponse`. Status 200. |
| `POST /auth/refresh` | `@Public()` | 30/60s | Body `RefreshTokenDto`. Returns `RefreshResponse`. Status 200. |
| `POST /auth/change-password` | Bearer + `@AllowUnverified()` | — | Body `ChangePasswordDto`. Void/200. Revokes **all** the user's refresh tokens on success. |
| `POST /auth/email-verification/request` | Bearer + `@AllowUnverified()` | 3/15min | No body. Void/200. |
| `POST /auth/email-verification/verify` | Bearer + `@AllowUnverified()` | 10/15min | Body `VerifyEmailDto`. Void/200. |
| `POST /auth/forgot-password` | `@Public()` | 5/15min | Body `ForgotPasswordDto`. Void/200 always (no email enumeration). |
| `POST /auth/reset-password` | `@Public()` | 10/15min | Body `ResetPasswordDto`. Void/200. Revokes all refresh tokens on success. |

**DTOs:**
- `SignUpDto`: `username` `@Length(3,50) @Matches(/^[A-Za-z0-9_.]+$/)`; `password` `@MinLength(8) @MaxLength(128)`; `email` `@IsEmail() @MaxLength(320)`.
- `LoginDto`: `username: string`, `password: string` (plain `@IsString()`, no length rule).
- `RefreshTokenDto`: `refreshToken: string`; `grantType?: 'refresh_token'` (`@IsIn(['refresh_token'])`, optional).
- `ChangePasswordDto`: `currentPassword: string`; `newPassword` `@MinLength(8) @MaxLength(128)`.
- `ForgotPasswordDto`: `email` `@IsEmail()`.
- `ResetPasswordDto`: `email` `@IsEmail()`; `code` `@Matches(/^\d{6}$/)`; `newPassword` `@MinLength(8) @MaxLength(128)`.
- `VerifyEmailDto`: `code` `@Matches(/^\d{6}$/)`.

**Response shapes:**
- `AuthResponse` (signup/login): `{ userId: string, authToken: string, refreshToken: string }` — key is **`authToken`**, not `accessToken`.
- `RefreshResponse`: `{ accessToken: string, refreshToken: string }` — here it **is** `accessToken`. (Deliberately called out: this naming is inconsistent between endpoints.)

**Business rules:**
- Access tokens: TTL `JWT_ACCESS_TTL_SECONDS` (default 900s/15min). Refresh tokens: TTL
  `JWT_REFRESH_TTL_SECONDS` (default 30 days); stored hashed server-side, **single-use /
  rotated** — every successful refresh revokes the presented token and issues a new one.
  Reusing a revoked/expired refresh token → 401.
- Signup rejects if username OR email already taken (409). New user starts at
  `onboardingState = "Profile_Image"`.
- Verification/reset codes: 6-digit numeric, bcrypt-hashed, 15-min expiry, max 3 issuances
  per 15 min (silent no-op past that), max 5 wrong guesses before auto-invalidating.
- Login rejects banned accounts with 401 (not 403).

## Onboarding (`/onboarding`) — Bearer + verified email required

| Method & Path | Notes |
|---|---|
| `POST /onboarding/image` | Body `{ imageUrl: string }` (`@IsUrl({require_tld:false}) @MaxLength(2048)`). Must be a completed upload owned by the caller. Advances `Profile_Image → Profile_Bio` only if currently `Profile_Image`. Void/200. |
| `POST /onboarding/bio` | Body `{ bio: string }` (`@MaxLength(500)`). Advances to `Profile_Audio` if currently `Profile_Image` or `Profile_Bio`. Void/200. |
| `POST /onboarding/audio` | Body `{ audioUrl: string }` (`@IsUrl({require_tld:false}) @MaxLength(2048)`). Must be a completed upload. Advances to `Profile_Interests` unless already `Complete`. Void/200. |
| `GET /onboarding/interests` | Returns `{ categories: {id, label}[] }` — active categories only, ordered by `sortOrder,label`. |
| `POST /onboarding/interests` | Body `{ categoryIds: string[] }` (`@ArrayMinSize(0) @ArrayMaxSize(20)`) — **0 is valid**; this is the terminal step that flips state to `Complete` regardless, so it must still be called to "skip". Replaces the entire interest set. 400 if any id is unknown/inactive. Void/200. |

## Profile (`/profile`) — Bearer + verified

| Method & Path | Query | Notes |
|---|---|---|
| `GET /profile` | `cursor?`, `limit?` (default 10) | Own profile → `ProfileObject`. |
| `GET /profile/:userId` | same | Another user's (or own) profile → `ProfileObject`. 404 if missing/banned/blocked either-way. |
| `PATCH /profile/edit` | body `UpdateProfileDto` | Partial update, 200 void. |

`UpdateProfileDto` (all optional/nullable — **sending `null` is treated as "no change", same as omitting**; there is no way to clear a field via this endpoint, only to set a new value):
- `bio?: string | null` (`@MaxLength(5000)`)
- `profileImageUrl?: string | null` (`@MaxLength(2048)`, must be a completed upload owned by the caller)
- `audioBioUrl?: string | null` (same upload requirement)
- `mentions?: MentionDto[] | null` — replaces bio mentions entirely; if non-empty, requires non-empty `bio`; each range must fit within the bio text and reference a real user, else 400.

## Users (`/user`, `/users`, `/search`) — Bearer + verified except where noted

| Method & Path | Auth notes | Notes |
|---|---|---|
| `DELETE /user/account` | `@AllowUnverified()` | Body `{ password }` (`@MinLength(8) @MaxLength(128)`). 401 if wrong. **204 No Content.** Pseudo-anonymizes the account (username → `deleted_<uuid>`, PII nulled, banned+private+deletedAt set), hard-deletes their reposts, content-nulls their posts/comments/replies, purges all social-graph rows, enqueues async S3 cleanup. |
| `POST /user/:userId/follow` | | Self-follow → 400. Target missing/banned → 404. Blocked either-way → 403. Private target → creates a follow **request** instead (idempotent upsert); public target → follows immediately. Idempotent if already following. 200. |
| `DELETE /user/:userId/follow` | | Removes the follow and any pending request. Always 200 (no-op if none existed). |
| `POST /user/:userId/report` | | Body `{ reason }` (`@MinLength(3) @MaxLength(1000)`, trimmed + one layer of wrapping quotes stripped). `ReportEntity{targetType:'USER'}`. 200. |
| `POST /user/:userId/block` | | Self-block → 400. Deletes follow/follow-request rows in **both directions** as part of the same transaction. 200. |
| `DELETE /user/:userId/block` | | 200, no-op if not blocked. |
| `GET /user/blocked` | `cursor?`, `limit?` (default 20) | `{ users: RelationshipUser[], nextPageKey }`. |
| `GET /user/privacy` | | `{ isPrivate: boolean }`. |
| `PATCH /user/privacy` | body `{ isPrivate: boolean }` | 200 void. |
| `GET /user/:userId/listeners` | `cursor?`, `limit?` (default 20) | Followers of `:userId`. `{ users: RelationshipUser[], nextPageKey }`. Blocked-either-way entries are filtered from the page but still count against the pagination window (a page can come back shorter than `limit`). 404 if target missing/banned. |
| `GET /user/:userId/listening` | same | Who `:userId` follows. Same shape/behavior. |
| `GET /user/follow-requests` | | Incoming requests. `{ requests: {userId, username, isVerified, profileImageUrl, audioBioUrl}[] }`, newest-first, capped at 200, **not paginated**. |
| `POST /user/:userId/follow-request/accept` | | Blocked either-way → 403. No-op 200 if no such request. On success: creates follow, deletes request, sends requester a `REQUEST_ACCEPTED` notification. |
| `DELETE /user/:userId/follow-request` | | Denies/cancels. 200, no-op if none. |
| `GET /users/search` | `query?` (default `''`), `limit?` | `{ users: SearchUser[] }`. Empty query → `[]`. Matches `username` OR `displayName` ILIKE, excludes viewer + blocked-either-way, exact-match sorted first. `limit` clamped `[1,50]`, default 10. |
| `GET /search` | `query?`, `usersLimit?`(10), `hashtagsLimit?`(10), `postsLimit?`(10) | `{ users: SearchUser[], hashtags: {hashtag, postCount, description, isFollowing}[], posts: PostObject[] }`. Leading `#` stripped from query. Empty query → all empty. |
| `POST /users/:userId/play` | body `{ listenedMs, durationMs }` (each `@IsInt @Min` per type, ≤ 86,400,000) | Records a voice-bio play. Blocked either-way → 403; target missing/banned → 404. Always logs the play; increments `audioBioPlayCount` only if it "qualifies": `durationMs <= 5000` OR `listenedMs >= clamp(durationMs*0.25, 1000, 3000)`. Void/200. |

## Uploads — no `/uploads` prefix on the presign/complete paths themselves

| Method & Path | Notes |
|---|---|
| `POST /preSignUrl` | Bearer. Body `{ type: 'image'\|'audio', purpose: 'profile'\|'post'\|'temp'\|'comment', sizeBytes: number }` (`@IsInt @Min(1)`). 400 if `sizeBytes` exceeds `UPLOAD_IMAGE_MAX_BYTES` (default 10MB) / `UPLOAD_AUDIO_MAX_BYTES` (default 25MB). Returns `{ url (presigned S3 PUT, TTL=UPLOAD_URL_TTL_SECONDS default 900s), fileName (the stable value to send back as imageUrl/audioUrl elsewhere), contentType ('image/jpeg' or 'audio/mp4' - server always forces exactly these regardless of the real file format), sizeBytes }`. Also inserts a `PENDING` row. |
| `POST /uploads/complete` | Bearer. Body `{ fileName }` — exactly what `preSignUrl` returned. Call **after** PUTting the file to the presigned URL. Server verifies the S3 object (size/content-type/owner metadata), validates the actual magic bytes match the declared type (mismatched content → 400), probes audio duration. Marks the row `READY`. Idempotent. **A `fileName` is unusable anywhere else (post/comment/profile create) until this completes** — those endpoints 400 if it isn't a `READY` row owned by the caller. |

**Upload flow is always 3 steps:** `POST /preSignUrl` → PUT raw bytes to the returned `url`
yourself (direct-to-S3, not proxied) → `POST /uploads/complete` with the `fileName`. Only
then is that `fileName` usable in any other endpoint's `imageUrl`/`audioUrl` field.

## Posts (`/post`, `/quote`, `/posts/*`) — Bearer + verified

| Method & Path | Throttle | Notes |
|---|---|---|
| `POST /post` | 10/60s | Body `CreatePostDto`. Creates a `POST`. 201 void. |
| `POST /quote` | 10/60s | Body `CreateQuoteDto` (`CreatePostDto` + `sourcePostId`). Source must be visible (404 otherwise); canonicalized to the underlying original first if it's itself a repost. 201 void. |
| `GET /posts/:postId` | — | `PostObject`. 404 if not visible or nonexistent. |
| `POST /posts/:postId/like` | — | Idempotent upsert; resolves reposts to their source first. Void/200. |
| `POST /posts/:postId/dislike` | — | Un-like. Void/200. |
| `DELETE /posts/:postId` | — | Author-only (403 otherwise); no-op 200 if missing. `REPOST` rows are hard-deleted; everything else soft-deletes (`postStatus=DELETED`, content nulled, row kept). |
| `POST /posts/:postId/report` | — | Body `{ reason }`. 404 if post doesn't exist (existence only, no visibility check). `ReportEntity{targetType:'POST'}`. Void/200. |
| `POST /posts/:postId/repost` | — | Creates a `REPOST` of the canonical source (repost-of-a-repost reposts the original). No-op 200 if an `AVAILABLE` repost already exists. |
| `DELETE /posts/:postId/repost` | — | Deletes the caller's own repost of the canonical source. Always 200. |
| `POST /posts/:postId/play` | — | Body `{ listenedMs, durationMs }` (same shape as profile play). Resolves to canonical source first; same "qualifies as play" rule increments `audioPlayCount`. Void/200. |

`CreatePostDto`:
```
audioUrl: string        @MaxLength(2048)              REQUIRED - every post must have audio
imageUrl?: string | null  @MaxLength(2048)
description?: string | null @MaxLength(5000)          → becomes `content` (trimmed; empty → null)
categoryId?: string | null @IsUUID()                    must be an active category, else 400
mentions?: MentionDto[] | null                          requires non-empty description; ranges validated same as elsewhere
```
`CreateQuoteDto` = `CreatePostDto` + `sourcePostId: string @IsUUID()` (required).

Creation also auto-extracts `#hashtag` tokens from `content` (`#([\p{L}\p{N}_]{1,100})`),
upserting `HashtagEntity` rows.

## Feed (`GET /feed`) — Bearer + verified

`GET /feed?cursor=&limit=` (limit default 20; clamped `[1,100]`).

```
{
  posts: PostObject[],                 // caller + everyone they follow
  trendingHashtags: [] | {hashtag, postCount, description, isFollowing}[],
  categories: [] | {id, label, sortOrder, isActive}[],
  nextPageKey: string | null
}
```
**`trendingHashtags`/`categories` are only populated on the first page (no `cursor`) —
every paginated call returns them empty**, to avoid recomputing global aggregates on scroll.

## Explore (`/explore`) — Bearer + verified

| Method & Path | Notes |
|---|---|
| `GET /explore/landing` | No pagination. `{ forYouPosts: PostObject[] (up to 30), trendingHashtags: {hashtag, postCount}[], channels: {userId, username, displayName, listenerCount, profession, profileImageUrl, profileAudioUrl, isVerified, isListening}[] (top 12 by follower count) }`. |
| `GET /explore/category?categoryId=&cursor=&limit=` | 404 if category missing/inactive. `{ categoryId, categoryLabel, categoryDescription, posts: PostObject[], postCount: number\|null, followersCount: number\|null, nextPageKey }` — **`postCount`/`followersCount` are `null` on any paginated call**, first-page only, same pattern as feed. |
| `POST /explore/category/follow?categoryId=` | 404 if category missing/inactive. Upsert. Void/200. |
| `DELETE /explore/category/follow?categoryId=` | 200, no-op if not followed. |
| `GET /explore/hashtags/:hashtag?limit=` | `:hashtag` URL-decoded, `#`-stripped, lowercased, truncated to 100 chars. Unknown tag → 200 placeholder `{hashtag, postCount:0, description:null, followedByText:null, isFollowing:false, posts:[], people:[]}` (not 404). Otherwise `{hashtag, postCount (total, unfiltered by status), description, followedByText: string\|null, isFollowing, posts: PostObject[] (AVAILABLE only, limit clamped [1,100] default 50), people: {userId, username, displayName, matchingPosts, profileImageUrl, audioBioUrl, isVerified, isFollowing}[]}`. |
| `POST /explore/hashtags/:hashtag/follow` | Creates the hashtag row if new. Upsert. Void/200. |
| `DELETE /explore/hashtags/:hashtag/follow` | No-op if missing. Void/200. |

## Comments & Replies — Bearer + verified

| Method & Path | Throttle | Notes |
|---|---|---|
| `POST /comments` | 20/60s | Body `CreateCommentDto`. `postId` must be visible (404 otherwise). Requires at least one of `text`/`imageUrl`/`audioUrl` (400 otherwise). Notifies the post's author (`POST_REPLY`), skipped if self. 201 void. |
| `POST /comments/:commentId/replies` | 20/60s | Body `CreateReplyDto` (+ optional `replyingToUserId`). Comment's post must be visible. Same content-required rule. Notifies `replyingToUserId` if given, else the comment's author. 201 void. |
| `GET /posts/:postId/comments` | — | `limit?` (default 20, ≤100), `cursor?`. Post must be visible (404, **not** empty list, if it isn't). `{ comments: CommentObject[], nextCursor, hasMore }`, newest-first. |
| `GET /comments/:commentId/replies` | — | Same query shape. `{ replies: ReplyObject[], nextCursor, hasMore }`, **oldest-first**. 404 if comment/post not visible. |
| `POST /comments/:commentId/like` | — | Upsert. 404 if not visible. Void/200. |
| `POST /comments/:commentId/dislike` | — | Delete. Void/200. |
| `DELETE /comments/:commentId` | — | Author-only (403); no-op 200 if missing. **Hard delete.** |
| `POST /comments/:commentId/report` | — | Body `{ reason }`. `ReportEntity{targetType:'COMMENT'}`. Void/200. |
| `POST /reply/:replyId/like` | — | Upsert. Void/200. |
| `POST /reply/:replyId/dislike` | — | Delete. Void/200. |
| `DELETE /reply/:replyId` | — | Author-only. **Hard delete.** |
| `POST /reply/:replyId/report` | — | `ReportEntity{targetType:'REPLY'}`. Void/200. |

`CreateCommentDto`: `postId: @IsUUID()` (required); `text?/imageUrl?/audioUrl?: string | null` (`@MaxLength` 5000/2048/2048); `mentions?: MentionDto[] | null`.
`CreateReplyDto`: same content fields (no `postId` — from path) + `replyingToUserId?: string | null @IsUUID()`.

## Notifications (`/notifications`) — Bearer + verified

| Method & Path | Notes |
|---|---|
| `GET /notifications?cursor=&limit=` | limit default 20, clamped [1,100]. `{ items: NotificationItem[], nextPageKey: string\|null }`. |
| `GET /notifications/unread-count` | `{ unreadCount: number }`. |
| `POST /notifications/read` | Body `{ ids: string[] }` (`@ArrayMaxSize(100)`). Only the caller's own matching notifications are marked; others silently ignored. Void/200. |
| `POST /notifications/read-all` | Marks every unread notification for the caller. Void/200. |
| `POST /notifications/fcm-token` | Body `{ token (@MaxLength(4096)), platform ('android'\|'ios'\|'ANDROID'\|'IOS', default 'android') }`. If the token already exists for another user, it's **reassigned** to the caller (handles device/account switches). Stored uppercased. Void/200. |
| `POST /notifications/fcm-token/unregister` | Body `{ token }`. Deletes the caller's row for it. Void/200. |

Notifications are only ever created server-side — no public "create notification" endpoint.
Every creation enqueues an `FCM_NOTIFICATION` background job (push delivery is async, not
synchronous with the triggering request). Self-notifications are suppressed.

## Moderation / Admin (`/admin`) — Bearer + verified + `AdminGuard` (`isAdmin=true`)

| Method & Path | Notes |
|---|---|
| `GET /admin/reports?status=OPEN&limit=` | `status` one of `OPEN/RESOLVED/DISMISSED` (case-insensitive, else 400). `limit` clamped [1,200], default 50. `{ reports: ReportEntity[] }` — raw entity rows, oldest-first. |
| `POST /admin/moderation/:targetType/:targetId` | Body `{ action, reason (@MinLength(3) @MaxLength(1000)) }`. `:targetType` case-insensitive, one of `USER/POST/COMMENT/REPLY`. `USER`→BAN/UNBAN only; `POST`→REMOVE/RESTORE only (REMOVE nulls content, RESTORE does **not** bring nulled content back); `COMMENT`/`REPLY`→REMOVE only (soft-delete, no restore). REMOVE with media enqueues async S3 cleanup. Every action logged to `ModerationActionEntity`. Void/200. |
| `POST /admin/reports/:reportId/resolve` | Body `{ resolution: 'RESOLVED'\|'DISMISSED', reason }`. 404 if missing. Sets status + resolver + timestamp; also logs a `ModerationActionEntity{targetType:'REPORT'}`. Void/200. |

## Health (`/health`) — `@Public()`, no auth

| Method & Path | Notes |
|---|---|
| `GET /health` | `{ status:'ok', service:'channel-service', timestamp }` — liveness only, no DB check. |
| `GET /health/ready` | Runs `SELECT 1`; success → `{status:'ready', service, database:'ok', timestamp}`; DB failure → **503**. |

---

## Things the Android client specifically needs to know

1. **Field-name inconsistency**: login/signup return `authToken`; refresh returns
   `accessToken`. Both are access tokens for the `Authorization: Bearer` header — don't
   assume one field name works for both response types.
2. **401 is the universal auth-failure signal** (missing/expired/wrong-type token, banned,
   deleted) by deliberate design — always try a token refresh on 401, not just when you
   expect "expired."
3. **403 after a valid token** almost always means the email-verification gate — check
   `/auth/status`'s `isEmailVerified` before assuming a real permissions error, except for
   follow/block/admin-specific 403s that are documented per-endpoint above.
4. **All signed media URLs expire** (`MEDIA_READ_URL_TTL_SECONDS`, default 1h) — never
   persist one as a stable value; re-fetch the parent resource for a fresh URL. The
   **`fileName`** returned by `/preSignUrl` (and echoed back as e.g. `imageUrl` on other
   endpoints) is the stable value to store/send — it is a managed key, not the signed URL.
5. **Uploads are always 3 steps**: `POST /preSignUrl` → PUT bytes directly to the returned
   `url` (not proxied through this API) → `POST /uploads/complete` with the `fileName`. Not
   usable elsewhere until `complete` succeeds.
6. **`postStatus: MODERATED` is overloaded** — "admin removed this" and "you can't see this
   (blocked/private)" are indistinguishable from this field alone. Don't build
   moderation-specific UI on it without another signal.
7. **Pagination cursors are opaque** — always echo back exactly what the server returned
   (`nextPageKey`/`nextCursor`); never construct one client-side. An invalid cursor is a
   400, not silently ignored.
8. **Reposts canonicalize transparently**: liking/reporting/playing/reposting a repost
   redirects server-side to the original post. A client interacting with a displayed repost
   should just use that post's own id — the server resolves canonicalization internally.
9. **Rate limits to design around**: signup/login 10/min, refresh 30/min,
   forgot/reset-password 5–10 per 15 min, email verification 3 requests/15min + 10
   verify-attempts/15min, post/quote create 10/min, comment/reply create 20/min; everything
   else falls under the global 120 req/min per IP.

## Source files (for direct reference / re-verifying this doc)

- Guards/interceptors: `src/common/auth/{jwt-auth,verified-email,admin}.guard.ts`,
  `src/common/auth/{public,allow-unverified}.decorator.ts`,
  `src/uploads/media-url.interceptor.ts`,
  `src/common/http/{api-exception.filter,request-logging.interceptor,request-context}.ts`
- Entities/enums: `src/database/entities.ts`, `src/database/extra-entities.ts`
- Pagination helpers: `src/common/pagination.ts`
- Shared post hydration (the canonical post shape): `src/posts/post-read.service.ts`
- Controllers/DTOs/services per feature:
  `src/{auth,onboarding,profiles,users,uploads,posts,feed,explore,comments,notifications,moderation,health}/*.controller.ts|*.dto.ts|*.service.ts`
- Global wiring: `src/app.module.ts`, `src/main.ts`
- TTLs/limits referenced above: `src/config/env.validation.ts`
