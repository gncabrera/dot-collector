package com.nookx.api.domain;

import static com.nookx.api.domain.MegaAssetTestSamples.*;
import static com.nookx.api.domain.MegaPartTestSamples.*;
import static com.nookx.api.domain.MegaSetTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaAssetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaAsset.class);
        MegaAsset megaAsset1 = getMegaAssetSample1();
        MegaAsset megaAsset2 = new MegaAsset();
        assertThat(megaAsset1).isNotEqualTo(megaAsset2);

        megaAsset2.setId(megaAsset1.getId());
        assertThat(megaAsset1).isEqualTo(megaAsset2);

        megaAsset2 = getMegaAssetSample2();
        assertThat(megaAsset1).isNotEqualTo(megaAsset2);
    }

    @Test
    void setTest() {
        MegaAsset megaAsset = getMegaAssetRandomSampleGenerator();
        MegaSet megaSetBack = getMegaSetRandomSampleGenerator();

        megaAsset.setSet(megaSetBack);
        assertThat(megaAsset.getSet()).isEqualTo(megaSetBack);

        megaAsset.set(null);
        assertThat(megaAsset.getSet()).isNull();
    }

    @Test
    void partTest() {
        MegaAsset megaAsset = getMegaAssetRandomSampleGenerator();
        MegaPart megaPartBack = getMegaPartRandomSampleGenerator();

        megaAsset.setPart(megaPartBack);
        assertThat(megaAsset.getPart()).isEqualTo(megaPartBack);

        megaAsset.part(null);
        assertThat(megaAsset.getPart()).isNull();
    }
}
