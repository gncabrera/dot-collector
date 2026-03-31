import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaAttributeOption, NewMegaAttributeOption } from '../mega-attribute-option.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaAttributeOption for edit and NewMegaAttributeOptionFormGroupInput for create.
 */
type MegaAttributeOptionFormGroupInput = IMegaAttributeOption | PartialWithRequiredKeyOf<NewMegaAttributeOption>;

type MegaAttributeOptionFormDefaults = Pick<NewMegaAttributeOption, 'id'>;

type MegaAttributeOptionFormGroupContent = {
  id: FormControl<IMegaAttributeOption['id'] | NewMegaAttributeOption['id']>;
  label: FormControl<IMegaAttributeOption['label']>;
  value: FormControl<IMegaAttributeOption['value']>;
  description: FormControl<IMegaAttributeOption['description']>;
  attribute: FormControl<IMegaAttributeOption['attribute']>;
};

export type MegaAttributeOptionFormGroup = FormGroup<MegaAttributeOptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaAttributeOptionFormService {
  createMegaAttributeOptionFormGroup(megaAttributeOption?: MegaAttributeOptionFormGroupInput): MegaAttributeOptionFormGroup {
    const megaAttributeOptionRawValue = {
      ...this.getFormDefaults(),
      ...(megaAttributeOption ?? { id: null }),
    };
    return new FormGroup<MegaAttributeOptionFormGroupContent>({
      id: new FormControl(
        { value: megaAttributeOptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      label: new FormControl(megaAttributeOptionRawValue.label),
      value: new FormControl(megaAttributeOptionRawValue.value),
      description: new FormControl(megaAttributeOptionRawValue.description),
      attribute: new FormControl(megaAttributeOptionRawValue.attribute),
    });
  }

  getMegaAttributeOption(form: MegaAttributeOptionFormGroup): IMegaAttributeOption | NewMegaAttributeOption {
    return form.getRawValue() as IMegaAttributeOption | NewMegaAttributeOption;
  }

  resetForm(form: MegaAttributeOptionFormGroup, megaAttributeOption: MegaAttributeOptionFormGroupInput): void {
    const megaAttributeOptionRawValue = { ...this.getFormDefaults(), ...megaAttributeOption };
    form.reset({
      ...megaAttributeOptionRawValue,
      id: { value: megaAttributeOptionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaAttributeOptionFormDefaults {
    return {
      id: null,
    };
  }
}
