package com.dot.collector.api.domain;

import static com.dot.collector.api.domain.MegaPartSubPartCountTestSamples.*;
import static com.dot.collector.api.domain.MegaPartTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dot.collector.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaPartSubPartCountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaPartSubPartCount.class);
        MegaPartSubPartCount megaPartSubPartCount1 = getMegaPartSubPartCountSample1();
        MegaPartSubPartCount megaPartSubPartCount2 = new MegaPartSubPartCount();
        assertThat(megaPartSubPartCount1).isNotEqualTo(megaPartSubPartCount2);

        megaPartSubPartCount2.setId(megaPartSubPartCount1.getId());
        assertThat(megaPartSubPartCount1).isEqualTo(megaPartSubPartCount2);

        megaPartSubPartCount2 = getMegaPartSubPartCountSample2();
        assertThat(megaPartSubPartCount1).isNotEqualTo(megaPartSubPartCount2);
    }

    @Test
    void partTest() {
        MegaPartSubPartCount megaPartSubPartCount = getMegaPartSubPartCountRandomSampleGenerator();
        MegaPart megaPartBack = getMegaPartRandomSampleGenerator();

        megaPartSubPartCount.setPart(megaPartBack);
        assertThat(megaPartSubPartCount.getPart()).isEqualTo(megaPartBack);

        megaPartSubPartCount.part(null);
        assertThat(megaPartSubPartCount.getPart()).isNull();
    }

    @Test
    void parentPartTest() {
        MegaPartSubPartCount megaPartSubPartCount = getMegaPartSubPartCountRandomSampleGenerator();
        MegaPart megaPartBack = getMegaPartRandomSampleGenerator();

        megaPartSubPartCount.setParentPart(megaPartBack);
        assertThat(megaPartSubPartCount.getParentPart()).isEqualTo(megaPartBack);

        megaPartSubPartCount.parentPart(null);
        assertThat(megaPartSubPartCount.getParentPart()).isNull();
    }
}
