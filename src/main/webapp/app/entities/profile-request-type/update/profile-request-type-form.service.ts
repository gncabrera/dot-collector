import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProfileRequestType, NewProfileRequestType } from '../profile-request-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfileRequestType for edit and NewProfileRequestTypeFormGroupInput for create.
 */
type ProfileRequestTypeFormGroupInput = IProfileRequestType | PartialWithRequiredKeyOf<NewProfileRequestType>;

type ProfileRequestTypeFormDefaults = Pick<NewProfileRequestType, 'id'>;

type ProfileRequestTypeFormGroupContent = {
  id: FormControl<IProfileRequestType['id'] | NewProfileRequestType['id']>;
  key: FormControl<IProfileRequestType['key']>;
  name: FormControl<IProfileRequestType['name']>;
  description: FormControl<IProfileRequestType['description']>;
};

export type ProfileRequestTypeFormGroup = FormGroup<ProfileRequestTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfileRequestTypeFormService {
  createProfileRequestTypeFormGroup(profileRequestType?: ProfileRequestTypeFormGroupInput): ProfileRequestTypeFormGroup {
    const profileRequestTypeRawValue = {
      ...this.getFormDefaults(),
      ...(profileRequestType ?? { id: null }),
    };
    return new FormGroup<ProfileRequestTypeFormGroupContent>({
      id: new FormControl(
        { value: profileRequestTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      key: new FormControl(profileRequestTypeRawValue.key),
      name: new FormControl(profileRequestTypeRawValue.name),
      description: new FormControl(profileRequestTypeRawValue.description),
    });
  }

  getProfileRequestType(form: ProfileRequestTypeFormGroup): IProfileRequestType | NewProfileRequestType {
    return form.getRawValue() as IProfileRequestType | NewProfileRequestType;
  }

  resetForm(form: ProfileRequestTypeFormGroup, profileRequestType: ProfileRequestTypeFormGroupInput): void {
    const profileRequestTypeRawValue = { ...this.getFormDefaults(), ...profileRequestType };
    form.reset({
      ...profileRequestTypeRawValue,
      id: { value: profileRequestTypeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProfileRequestTypeFormDefaults {
    return {
      id: null,
    };
  }
}
