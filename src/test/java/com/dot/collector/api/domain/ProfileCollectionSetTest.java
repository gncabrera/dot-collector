package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.MegaSetTestSamples.*;
import static com.dot.collector.api.domain.ProfileCollectionSetTestSamples.*;
import static com.dot.collector.api.domain.ProfileCollectionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProfileCollectionSetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileCollectionSet.class);
        ProfileCollectionSet profileCollectionSet1 = getProfileCollectionSetSample1();
        ProfileCollectionSet profileCollectionSet2 = new ProfileCollectionSet();
        assertThat(profileCollectionSet1).isNotEqualTo(profileCollectionSet2);

        profileCollectionSet2.setId(profileCollectionSet1.getId());
        assertThat(profileCollectionSet1).isEqualTo(profileCollectionSet2);

        profileCollectionSet2 = getProfileCollectionSetSample2();
        assertThat(profileCollectionSet1).isNotEqualTo(profileCollectionSet2);
    }

    @Test
    void collectionTest() {
        ProfileCollectionSet profileCollectionSet = getProfileCollectionSetRandomSampleGenerator();
        ProfileCollection profileCollectionBack = getProfileCollectionRandomSampleGenerator();

        profileCollectionSet.setCollection(profileCollectionBack);
        assertThat(profileCollectionSet.getCollection()).isEqualTo(profileCollectionBack);

        profileCollectionSet.collection(null);
        assertThat(profileCollectionSet.getCollection()).isNull();
    }

    @Test
    void setTest() {
        ProfileCollectionSet profileCollectionSet = getProfileCollectionSetRandomSampleGenerator();
        MegaSet megaSetBack = getMegaSetRandomSampleGenerator();

        profileCollectionSet.addSet(megaSetBack);
        assertThat(profileCollectionSet.getSets()).containsOnly(megaSetBack);

        profileCollectionSet.removeSet(megaSetBack);
        assertThat(profileCollectionSet.getSets()).doesNotContain(megaSetBack);

        profileCollectionSet.sets(new HashSet<>(Set.of(megaSetBack)));
        assertThat(profileCollectionSet.getSets()).containsOnly(megaSetBack);

        profileCollectionSet.setSets(new HashSet<>());
        assertThat(profileCollectionSet.getSets()).doesNotContain(megaSetBack);
    }
}
