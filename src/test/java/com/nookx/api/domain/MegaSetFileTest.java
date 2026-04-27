package com.nookx.api.domain;

import static com.nookx.api.domain.MegaAssetTestSamples.*;
import static com.nookx.api.domain.MegaSetFileTestSamples.*;
import static com.nookx.api.domain.MegaSetTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaSetFileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSetFile.class);
        MegaSetFile megaSetFile1 = getMegaSetFileSample1();
        MegaSetFile megaSetFile2 = new MegaSetFile();
        assertThat(megaSetFile1).isNotEqualTo(megaSetFile2);

        megaSetFile2.setId(megaSetFile1.getId());
        assertThat(megaSetFile1).isEqualTo(megaSetFile2);

        megaSetFile2 = getMegaSetFileSample2();
        assertThat(megaSetFile1).isNotEqualTo(megaSetFile2);
    }

    @Test
    void megaSetTest() {
        MegaSetFile megaSetFile = getMegaSetFileRandomSampleGenerator();
        MegaSet megaSetBack = getMegaSetRandomSampleGenerator();

        megaSetFile.setMegaSet(megaSetBack);
        assertThat(megaSetFile.getMegaSet()).isEqualTo(megaSetBack);

        megaSetFile.megaSet(null);
        assertThat(megaSetFile.getMegaSet()).isNull();
    }

    @Test
    void assetTest() {
        MegaSetFile megaSetFile = getMegaSetFileRandomSampleGenerator();
        MegaAsset megaAssetBack = getMegaAssetRandomSampleGenerator();

        megaSetFile.setAsset(megaAssetBack);
        assertThat(megaSetFile.getAsset()).isEqualTo(megaAssetBack);

        megaSetFile.asset(null);
        assertThat(megaSetFile.getAsset()).isNull();
    }
}
