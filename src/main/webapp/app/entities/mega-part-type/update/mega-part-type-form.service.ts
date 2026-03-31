import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaPartType, NewMegaPartType } from '../mega-part-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaPartType for edit and NewMegaPartTypeFormGroupInput for create.
 */
type MegaPartTypeFormGroupInput = IMegaPartType | PartialWithRequiredKeyOf<NewMegaPartType>;

type MegaPartTypeFormDefaults = Pick<NewMegaPartType, 'id' | 'active' | 'isLatest' | 'attributes'>;

type MegaPartTypeFormGroupContent = {
  id: FormControl<IMegaPartType['id'] | NewMegaPartType['id']>;
  name: FormControl<IMegaPartType['name']>;
  version: FormControl<IMegaPartType['version']>;
  active: FormControl<IMegaPartType['active']>;
  isLatest: FormControl<IMegaPartType['isLatest']>;
  attributes: FormControl<IMegaPartType['attributes']>;
};

export type MegaPartTypeFormGroup = FormGroup<MegaPartTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaPartTypeFormService {
  createMegaPartTypeFormGroup(megaPartType?: MegaPartTypeFormGroupInput): MegaPartTypeFormGroup {
    const megaPartTypeRawValue = {
      ...this.getFormDefaults(),
      ...(megaPartType ?? { id: null }),
    };
    return new FormGroup<MegaPartTypeFormGroupContent>({
      id: new FormControl(
        { value: megaPartTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(megaPartTypeRawValue.name, {
        validators: [Validators.required],
      }),
      version: new FormControl(megaPartTypeRawValue.version, {
        validators: [Validators.required],
      }),
      active: new FormControl(megaPartTypeRawValue.active),
      isLatest: new FormControl(megaPartTypeRawValue.isLatest),
      attributes: new FormControl(megaPartTypeRawValue.attributes ?? []),
    });
  }

  getMegaPartType(form: MegaPartTypeFormGroup): IMegaPartType | NewMegaPartType {
    return form.getRawValue() as IMegaPartType | NewMegaPartType;
  }

  resetForm(form: MegaPartTypeFormGroup, megaPartType: MegaPartTypeFormGroupInput): void {
    const megaPartTypeRawValue = { ...this.getFormDefaults(), ...megaPartType };
    form.reset({
      ...megaPartTypeRawValue,
      id: { value: megaPartTypeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaPartTypeFormDefaults {
    return {
      id: null,
      active: false,
      isLatest: false,
      attributes: [],
    };
  }
}
