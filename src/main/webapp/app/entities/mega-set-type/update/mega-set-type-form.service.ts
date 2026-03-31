import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaSetType, NewMegaSetType } from '../mega-set-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaSetType for edit and NewMegaSetTypeFormGroupInput for create.
 */
type MegaSetTypeFormGroupInput = IMegaSetType | PartialWithRequiredKeyOf<NewMegaSetType>;

type MegaSetTypeFormDefaults = Pick<NewMegaSetType, 'id' | 'active' | 'isLatest' | 'attributes'>;

type MegaSetTypeFormGroupContent = {
  id: FormControl<IMegaSetType['id'] | NewMegaSetType['id']>;
  name: FormControl<IMegaSetType['name']>;
  version: FormControl<IMegaSetType['version']>;
  active: FormControl<IMegaSetType['active']>;
  isLatest: FormControl<IMegaSetType['isLatest']>;
  attributes: FormControl<IMegaSetType['attributes']>;
};

export type MegaSetTypeFormGroup = FormGroup<MegaSetTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaSetTypeFormService {
  createMegaSetTypeFormGroup(megaSetType?: MegaSetTypeFormGroupInput): MegaSetTypeFormGroup {
    const megaSetTypeRawValue = {
      ...this.getFormDefaults(),
      ...(megaSetType ?? { id: null }),
    };
    return new FormGroup<MegaSetTypeFormGroupContent>({
      id: new FormControl(
        { value: megaSetTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(megaSetTypeRawValue.name, {
        validators: [Validators.required],
      }),
      version: new FormControl(megaSetTypeRawValue.version, {
        validators: [Validators.required],
      }),
      active: new FormControl(megaSetTypeRawValue.active),
      isLatest: new FormControl(megaSetTypeRawValue.isLatest),
      attributes: new FormControl(megaSetTypeRawValue.attributes ?? []),
    });
  }

  getMegaSetType(form: MegaSetTypeFormGroup): IMegaSetType | NewMegaSetType {
    return form.getRawValue() as IMegaSetType | NewMegaSetType;
  }

  resetForm(form: MegaSetTypeFormGroup, megaSetType: MegaSetTypeFormGroupInput): void {
    const megaSetTypeRawValue = { ...this.getFormDefaults(), ...megaSetType };
    form.reset({
      ...megaSetTypeRawValue,
      id: { value: megaSetTypeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaSetTypeFormDefaults {
    return {
      id: null,
      active: false,
      isLatest: false,
      attributes: [],
    };
  }
}
