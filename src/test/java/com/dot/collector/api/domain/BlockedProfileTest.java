package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.BlockedProfileTestSamples.*;
import static com.dot.collector.api.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BlockedProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlockedProfile.class);
        BlockedProfile blockedProfile1 = getBlockedProfileSample1();
        BlockedProfile blockedProfile2 = new BlockedProfile();
        assertThat(blockedProfile1).isNotEqualTo(blockedProfile2);

        blockedProfile2.setId(blockedProfile1.getId());
        assertThat(blockedProfile1).isEqualTo(blockedProfile2);

        blockedProfile2 = getBlockedProfileSample2();
        assertThat(blockedProfile1).isNotEqualTo(blockedProfile2);
    }

    @Test
    void profileTest() {
        BlockedProfile blockedProfile = getBlockedProfileRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        blockedProfile.setProfile(profileBack);
        assertThat(blockedProfile.getProfile()).isEqualTo(profileBack);

        blockedProfile.profile(null);
        assertThat(blockedProfile.getProfile()).isNull();
    }

    @Test
    void blockedProfileTest() {
        BlockedProfile blockedProfile = getBlockedProfileRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        blockedProfile.setBlockedProfile(profileBack);
        assertThat(blockedProfile.getBlockedProfile()).isEqualTo(profileBack);

        blockedProfile.blockedProfile(null);
        assertThat(blockedProfile.getBlockedProfile()).isNull();
    }
}
