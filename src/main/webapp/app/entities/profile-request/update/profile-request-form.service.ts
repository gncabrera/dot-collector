import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfileRequest, NewProfileRequest } from '../profile-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfileRequest for edit and NewProfileRequestFormGroupInput for create.
 */
type ProfileRequestFormGroupInput = IProfileRequest | PartialWithRequiredKeyOf<NewProfileRequest>;

type ProfileRequestFormDefaults = Pick<NewProfileRequest, 'id'>;

type ProfileRequestFormGroupContent = {
  id: FormControl<IProfileRequest['id'] | NewProfileRequest['id']>;
  message: FormControl<IProfileRequest['message']>;
  type: FormControl<IProfileRequest['type']>;
  profile: FormControl<IProfileRequest['profile']>;
};

export type ProfileRequestFormGroup = FormGroup<ProfileRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfileRequestFormService {
  createProfileRequestFormGroup(profileRequest?: ProfileRequestFormGroupInput): ProfileRequestFormGroup {
    const profileRequestRawValue = {
      ...this.getFormDefaults(),
      ...(profileRequest ?? { id: null }),
    };
    return new FormGroup<ProfileRequestFormGroupContent>({
      id: new FormControl(
        { value: profileRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      message: new FormControl(profileRequestRawValue.message),
      type: new FormControl(profileRequestRawValue.type),
      profile: new FormControl(profileRequestRawValue.profile),
    });
  }

  getProfileRequest(form: ProfileRequestFormGroup): IProfileRequest | NewProfileRequest {
    return form.getRawValue() as IProfileRequest | NewProfileRequest;
  }

  resetForm(form: ProfileRequestFormGroup, profileRequest: ProfileRequestFormGroupInput): void {
    const profileRequestRawValue = { ...this.getFormDefaults(), ...profileRequest };
    form.reset({
      ...profileRequestRawValue,
      id: { value: profileRequestRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfileRequestFormDefaults {
    return {
      id: null,
    };
  }
}
