package com.nookx.api.domain;

import static com.nookx.api.domain.ProfileRequestTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.nookx.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileRequestTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfileRequestType.class);
        ProfileRequestType profileRequestType1 = getProfileRequestTypeSample1();
        ProfileRequestType profileRequestType2 = new ProfileRequestType();
        assertThat(profileRequestType1).isNotEqualTo(profileRequestType2);

        profileRequestType2.setId(profileRequestType1.getId());
        assertThat(profileRequestType1).isEqualTo(profileRequestType2);

        profileRequestType2 = getProfileRequestTypeSample2();
        assertThat(profileRequestType1).isNotEqualTo(profileRequestType2);
    }
}
