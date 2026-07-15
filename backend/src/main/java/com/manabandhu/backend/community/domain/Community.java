package com.manabandhu.backend.community.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "communities")
public class Community extends com.manabandhu.backend.common.domain.BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private String category;

    @Column
    private String city;

    @Column
    private String state;

    @Column(nullable = false)
    private int memberCount;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "pinned_post_ids", nullable = false, columnDefinition = "uuid[] default '{}'")
    private UUID[] pinnedPostIds = new UUID[0];

    public Community() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    public UUID[] getPinnedPostIds() { return pinnedPostIds; }
    public void setPinnedPostIds(UUID[] pinnedPostIds) { this.pinnedPostIds = pinnedPostIds; }
}
