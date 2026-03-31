package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.MegaSetTestSamples.*;
import static com.dot.collector.api.domain.MegaSetTypeTestSamples.*;
import static com.dot.collector.api.domain.ProfileCollectionSetTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MegaSetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSet.class);
        MegaSet megaSet1 = getMegaSetSample1();
        MegaSet megaSet2 = new MegaSet();
        assertThat(megaSet1).isNotEqualTo(megaSet2);

        megaSet2.setId(megaSet1.getId());
        assertThat(megaSet1).isEqualTo(megaSet2);

        megaSet2 = getMegaSetSample2();
        assertThat(megaSet1).isNotEqualTo(megaSet2);
    }

    @Test
    void typeTest() {
        MegaSet megaSet = getMegaSetRandomSampleGenerator();
        MegaSetType megaSetTypeBack = getMegaSetTypeRandomSampleGenerator();

        megaSet.setType(megaSetTypeBack);
        assertThat(megaSet.getType()).isEqualTo(megaSetTypeBack);

        megaSet.type(null);
        assertThat(megaSet.getType()).isNull();
    }

    @Test
    void profileCollectionSetTest() {
        MegaSet megaSet = getMegaSetRandomSampleGenerator();
        ProfileCollectionSet profileCollectionSetBack = getProfileCollectionSetRandomSampleGenerator();

        megaSet.addProfileCollectionSet(profileCollectionSetBack);
        assertThat(megaSet.getProfileCollectionSets()).containsOnly(profileCollectionSetBack);
        assertThat(profileCollectionSetBack.getSets()).containsOnly(megaSet);

        megaSet.removeProfileCollectionSet(profileCollectionSetBack);
        assertThat(megaSet.getProfileCollectionSets()).doesNotContain(profileCollectionSetBack);
        assertThat(profileCollectionSetBack.getSets()).doesNotContain(megaSet);

        megaSet.profileCollectionSets(new HashSet<>(Set.of(profileCollectionSetBack)));
        assertThat(megaSet.getProfileCollectionSets()).containsOnly(profileCollectionSetBack);
        assertThat(profileCollectionSetBack.getSets()).containsOnly(megaSet);

        megaSet.setProfileCollectionSets(new HashSet<>());
        assertThat(megaSet.getProfileCollectionSets()).doesNotContain(profileCollectionSetBack);
        assertThat(profileCollectionSetBack.getSets()).doesNotContain(megaSet);
    }
}
