package com.example.learningwebflux.post.scheduledpost;

import com.example.learningwebflux.post.PostMedia;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScheduledPost {
    public ScheduledPost(
        String id,
        String userId,
        OffsetDateTime scheduledTime,
        List<PostTarget> targets,
        String title,
        String caption,
        List<PostMedia> postMedia,
        Boolean isFulfilled,

        String schedulerId
    ) {
        this.id = id;
        this.userId = userId;
        this.scheduledTime = scheduledTime;
        this.targets = new ArrayList<PostTarget>(targets);

        this.title = title;
        this.caption = caption;
        this.medias = new ArrayList<>(postMedia);
        this.isFulfilled = isFulfilled;

        this.schedulerId = schedulerId;
    }

    public OffsetDateTime getScheduledTime() { return scheduledTime; }

    public void changeScheduledTime(OffsetDateTime newTime) {
        if (newTime.isBefore(OffsetDateTime.now())) {
            throw new InvalidParameterException("Scheduled time needs to be in the future");
        }

        scheduledTime = newTime;
    }

    public List<PostTarget> getTargets() { return targets.stream().toList(); }

    public void addTarget(PostTarget target) throws Exception {
        var matchingPostTargets = targets
            .stream()
            .filter(e -> e.platformConnectionId.equals(target.platformConnectionId) && e.targetType.equals(target.targetType))
            .toList();

        if (!matchingPostTargets.isEmpty()) {
            throw new Exception("Post Target already exists");
        }

        targets.add(target);
    }

    public void removeTarget(String id) {
        targets.removeIf(e -> Objects.equals(e.id, id));
    }

    public String getEventBridgeId() { return eventBridgeScheduleId; }

    public void markAsScheduledOnEventBridge(String scheduleId) {
        this.eventBridgeScheduleId = scheduleId;
    }

    public String getTitle() { return title; }

    public void changeTitle(String newValue) {
        title = (newValue == null || newValue.isEmpty()) ? null : newValue;
    }

    public String getCaption() { return caption; }

    public void changeCaption(String newValue) {
        caption = (newValue == null || newValue.isEmpty()) ? null : newValue;
    }

    public void addMedia(PostMedia media) {
        medias.add(media);
    }

    public void removeMedia(String id) {
        medias.removeIf(postMedia -> postMedia.id().equals(id));
    }

    public boolean getIsFulfilled() { return isFulfilled; }

    public void markAsFulfilled() {
        isFulfilled = true;
    }

    public List<PostMedia> getMedias() { return medias.stream().toList(); }

    public String getSchedulerId() {
        return schedulerId;
    }

    public void markScheduled(String schedulerId) {
        this.schedulerId = schedulerId;
    }


    public final String id;
    public final String userId;
    private OffsetDateTime scheduledTime;

    private List<PostTarget> targets;

    private String eventBridgeScheduleId;

    private String title;
    private String caption;
    private final ArrayList<PostMedia> medias;
    private Boolean isFulfilled;

    private String schedulerId;


    public static ScheduledPost.Builder builder(String id, String userId, OffsetDateTime scheduledTime) {
        return new ScheduledPost.Builder(id, userId, scheduledTime);
    }



    public static class Builder {
        Builder(
            String id,
            String userId,
            OffsetDateTime scheduledTime
        ) {
            this.id = id;
            this.userId = userId;
            this.scheduledTime = scheduledTime;
        }

        public ScheduledPost build() throws Exception {
            if (targets.isEmpty()) { throw new Exception("Post targets cannot be empty"); }

            return new ScheduledPost(
                id,
                userId,
                scheduledTime,
                targets,
                title,
                caption,
                postMedias,
                false,

                null
            );
        }

        public ScheduledPost.Builder addPostTarget(PostTarget value) {
            targets.add(value);
            return this;
        }

        public ScheduledPost.Builder addPostTargets(PostTarget[] value) {
            targets.addAll(List.of(value));
            return this;
        }

        public ScheduledPost.Builder postTargets(PostTarget[] value) {
            targets = new ArrayList<>(List.of(value));
            return this;
        }

        public ScheduledPost.Builder title(String value) {
            title = value;
            return this;
        }

        public ScheduledPost.Builder caption(String value) {
            caption = value;
            return this;
        }

        public ScheduledPost.Builder addPostMedia(PostMedia toAdd) {
            postMedias.add(toAdd);
            return this;
        }

        public ScheduledPost.Builder addPostMedias(PostMedia[] toAdd) {
            postMedias.addAll(List.of(toAdd));
            return this;
        }

        public ScheduledPost.Builder postMedias(PostMedia[] value) {
            postMedias = new ArrayList<>(List.of(value));
            return this;
        }



        private final String id;
        private final String userId;
        private final OffsetDateTime scheduledTime;
        private ArrayList<PostTarget> targets = new ArrayList<PostTarget>();
        private String title;
        private String caption;
        private ArrayList<PostMedia> postMedias = new ArrayList<PostMedia>();
    }
}