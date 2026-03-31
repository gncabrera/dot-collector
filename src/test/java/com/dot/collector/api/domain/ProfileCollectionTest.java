package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.ProfileCollectionTestSamples.*;
import static com.dot.collector.api.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileCollectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileCollection.class);
        ProfileCollection profileCollection1 = getProfileCollectionSample1();
        ProfileCollection profileCollection2 = new ProfileCollection();
        assertThat(profileCollection1).isNotEqualTo(profileCollection2);

        profileCollection2.setId(profileCollection1.getId());
        assertThat(profileCollection1).isEqualTo(profileCollection2);

        profileCollection2 = getProfileCollectionSample2();
        assertThat(profileCollection1).isNotEqualTo(profileCollection2);
    }

    @Test
    void profileTest() {
        ProfileCollection profileCollection = getProfileCollectionRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        profileCollection.setProfile(profileBack);
        assertThat(profileCollection.getProfile()).isEqualTo(profileBack);

        profileCollection.profile(null);
        assertThat(profileCollection.getProfile()).isNull();
    }
}
