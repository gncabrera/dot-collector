package com.nookx.api.domain;

import static com.nookx.api.domain.MegaPartTestSamples.*;
import static com.nookx.api.domain.MegaSetPartCountTestSamples.*;
import static com.nookx.api.domain.MegaSetTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MegaSetPartCountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MegaSetPartCount.class);
        MegaSetPartCount megaSetPartCount1 = getMegaSetPartCountSample1();
        MegaSetPartCount megaSetPartCount2 = new MegaSetPartCount();
        assertThat(megaSetPartCount1).isNotEqualTo(megaSetPartCount2);

        megaSetPartCount2.setId(megaSetPartCount1.getId());
        assertThat(megaSetPartCount1).isEqualTo(megaSetPartCount2);

        megaSetPartCount2 = getMegaSetPartCountSample2();
        assertThat(megaSetPartCount1).isNotEqualTo(megaSetPartCount2);
    }

    @Test
    void setTest() {
        MegaSetPartCount megaSetPartCount = getMegaSetPartCountRandomSampleGenerator();
        MegaSet megaSetBack = getMegaSetRandomSampleGenerator();

        megaSetPartCount.setSet(megaSetBack);
        assertThat(megaSetPartCount.getSet()).isEqualTo(megaSetBack);

        megaSetPartCount.set(null);
        assertThat(megaSetPartCount.getSet()).isNull();
    }

    @Test
    void partTest() {
        MegaSetPartCount megaSetPartCount = getMegaSetPartCountRandomSampleGenerator();
        MegaPart megaPartBack = getMegaPartRandomSampleGenerator();

        megaSetPartCount.setPart(megaPartBack);
        assertThat(megaSetPartCount.getPart()).isEqualTo(megaPartBack);

        megaSetPartCount.part(null);
        assertThat(megaSetPartCount.getPart()).isNull();
    }
}
