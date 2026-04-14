package com.nookx.api.domain;

import static com.nookx.api.domain.MegaAssetTestSamples.*;
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
}
