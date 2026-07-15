package com.manabandhu.backend.profiles.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class ProfileEntity extends BaseEntity {

    @Column(nullable = false)
    private String displayName;

    @Column
    private String avatarUrl;

    @Column(length = 2000)
    private String bio;

    @Column
    private String city;

    @Column(length = 2)
    private String state;

    @Column(columnDefinition = "text[]")
    private String[] languages = new String[0];

    @Column
    private String occupation;

    @Enumerated(EnumType.STRING)
    @Column
    private Archetype archetype;

    @Column
    private String university;

    @Column
    private String employer;

    @Column(columnDefinition = "text[]")
    private String[] interests = new String[0];

    @Column(nullable = false)
    private int profileCompletionScore;

    @Column(nullable = false)
    private double ratingAverage;

    @Column(nullable = false)
    private int ratingCount;

    @Column(nullable = false)
    private int contributionLevel;

    @Column(columnDefinition = "jsonb")
    private String privacy; // json

    @Column(nullable = false)
    private boolean onboardingCompleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus = AccountStatus.active;

    public enum Archetype { student, professional, family, newcomer }

    public enum Role { user, moderator, admin }

    public enum AccountStatus { active, warned, suspended, banned, deleted }

    // getters/setters omitted for brevity but required by JPA via field access
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String v) { this.displayName = v; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String v) { this.avatarUrl = v; }
    public String getBio() { return bio; }
    public void setBio(String v) { this.bio = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getState() { return state; }
    public void setState(String v) { this.state = v; }
    public String[] getLanguages() { return languages; }
    public void setLanguages(String[] v) { this.languages = v; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String v) { this.occupation = v; }
    public Archetype getArchetype() { return archetype; }
    public void setArchetype(Archetype v) { this.archetype = v; }
    public String getUniversity() { return university; }
    public void setUniversity(String v) { this.university = v; }
    public String getEmployer() { return employer; }
    public void setEmployer(String v) { this.employer = v; }
    public String[] getInterests() { return interests; }
    public void setInterests(String[] v) { this.interests = v; }
    public int getProfileCompletionScore() { return profileCompletionScore; }
    public void setProfileCompletionScore(int v) { this.profileCompletionScore = v; }
    public double getRatingAverage() { return ratingAverage; }
    public void setRatingAverage(double v) { this.ratingAverage = v; }
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int v) { this.ratingCount = v; }
    public int getContributionLevel() { return contributionLevel; }
    public void setContributionLevel(int v) { this.contributionLevel = v; }
    public String getPrivacy() { return privacy; }
    public void setPrivacy(String v) { this.privacy = v; }
    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    public void setOnboardingCompleted(boolean v) { this.onboardingCompleted = v; }
    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus v) { this.accountStatus = v; }
}
