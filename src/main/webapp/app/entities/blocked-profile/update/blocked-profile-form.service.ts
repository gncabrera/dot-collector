import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBlockedProfile, NewBlockedProfile } from '../blocked-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBlockedProfile for edit and NewBlockedProfileFormGroupInput for create.
 */
type BlockedProfileFormGroupInput = IBlockedProfile | PartialWithRequiredKeyOf<NewBlockedProfile>;

type BlockedProfileFormDefaults = Pick<NewBlockedProfile, 'id'>;

type BlockedProfileFormGroupContent = {
  id: FormControl<IBlockedProfile['id'] | NewBlockedProfile['id']>;
  reason: FormControl<IBlockedProfile['reason']>;
  dateBlocked: FormControl<IBlockedProfile['dateBlocked']>;
  profile: FormControl<IBlockedProfile['profile']>;
  blockedProfile: FormControl<IBlockedProfile['blockedProfile']>;
};

export type BlockedProfileFormGroup = FormGroup<BlockedProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BlockedProfileFormService {
  createBlockedProfileFormGroup(blockedProfile?: BlockedProfileFormGroupInput): BlockedProfileFormGroup {
    const blockedProfileRawValue = {
      ...this.getFormDefaults(),
      ...(blockedProfile ?? { id: null }),
    };
    return new FormGroup<BlockedProfileFormGroupContent>({
      id: new FormControl(
        { value: blockedProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reason: new FormControl(blockedProfileRawValue.reason),
      dateBlocked: new FormControl(blockedProfileRawValue.dateBlocked),
      profile: new FormControl(blockedProfileRawValue.profile),
      blockedProfile: new FormControl(blockedProfileRawValue.blockedProfile),
    });
  }

  getBlockedProfile(form: BlockedProfileFormGroup): IBlockedProfile | NewBlockedProfile {
    return form.getRawValue() as IBlockedProfile | NewBlockedProfile;
  }

  resetForm(form: BlockedProfileFormGroup, blockedProfile: BlockedProfileFormGroupInput): void {
    const blockedProfileRawValue = { ...this.getFormDefaults(), ...blockedProfile };
    form.reset({
      ...blockedProfileRawValue,
      id: { value: blockedProfileRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BlockedProfileFormDefaults {
    return {
      id: null,
    };
  }
}
