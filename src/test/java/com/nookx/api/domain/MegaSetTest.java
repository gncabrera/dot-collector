package com.nookx.api.domain;

import static com.nookx.api.domain.MegaSetTestSamples.*;
import static com.nookx.api.domain.MegaSetTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
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
}
