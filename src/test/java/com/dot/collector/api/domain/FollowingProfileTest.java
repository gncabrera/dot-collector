package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.FollowingProfileTestSamples.*;
import static com.dot.collector.api.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowingProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FollowingProfile.class);
        FollowingProfile followingProfile1 = getFollowingProfileSample1();
        FollowingProfile followingProfile2 = new FollowingProfile();
        assertThat(followingProfile1).isNotEqualTo(followingProfile2);

        followingProfile2.setId(followingProfile1.getId());
        assertThat(followingProfile1).isEqualTo(followingProfile2);

        followingProfile2 = getFollowingProfileSample2();
        assertThat(followingProfile1).isNotEqualTo(followingProfile2);
    }

    @Test
    void profileTest() {
        FollowingProfile followingProfile = getFollowingProfileRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        followingProfile.setProfile(profileBack);
        assertThat(followingProfile.getProfile()).isEqualTo(profileBack);

        followingProfile.profile(null);
        assertThat(followingProfile.getProfile()).isNull();
    }

    @Test
    void followedProfileTest() {
        FollowingProfile followingProfile = getFollowingProfileRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        followingProfile.setFollowedProfile(profileBack);
        assertThat(followingProfile.getFollowedProfile()).isEqualTo(profileBack);

        followingProfile.followedProfile(null);
        assertThat(followingProfile.getFollowedProfile()).isNull();
    }
}
