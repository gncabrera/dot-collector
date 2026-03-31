import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaAttribute, NewMegaAttribute } from '../mega-attribute.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaAttribute for edit and NewMegaAttributeFormGroupInput for create.
 */
type MegaAttributeFormGroupInput = IMegaAttribute | PartialWithRequiredKeyOf<NewMegaAttribute>;

type MegaAttributeFormDefaults = Pick<NewMegaAttribute, 'id' | 'required' | 'multiple' | 'active' | 'setTypes' | 'partTypes'>;

type MegaAttributeFormGroupContent = {
  id: FormControl<IMegaAttribute['id'] | NewMegaAttribute['id']>;
  name: FormControl<IMegaAttribute['name']>;
  label: FormControl<IMegaAttribute['label']>;
  description: FormControl<IMegaAttribute['description']>;
  uiComponent: FormControl<IMegaAttribute['uiComponent']>;
  type: FormControl<IMegaAttribute['type']>;
  required: FormControl<IMegaAttribute['required']>;
  multiple: FormControl<IMegaAttribute['multiple']>;
  defaultValue: FormControl<IMegaAttribute['defaultValue']>;
  minNumber: FormControl<IMegaAttribute['minNumber']>;
  maxNumber: FormControl<IMegaAttribute['maxNumber']>;
  minLength: FormControl<IMegaAttribute['minLength']>;
  maxLength: FormControl<IMegaAttribute['maxLength']>;
  regex: FormControl<IMegaAttribute['regex']>;
  order: FormControl<IMegaAttribute['order']>;
  attributeGroup: FormControl<IMegaAttribute['attributeGroup']>;
  active: FormControl<IMegaAttribute['active']>;
  setTypes: FormControl<IMegaAttribute['setTypes']>;
  partTypes: FormControl<IMegaAttribute['partTypes']>;
};

export type MegaAttributeFormGroup = FormGroup<MegaAttributeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaAttributeFormService {
  createMegaAttributeFormGroup(megaAttribute?: MegaAttributeFormGroupInput): MegaAttributeFormGroup {
    const megaAttributeRawValue = {
      ...this.getFormDefaults(),
      ...(megaAttribute ?? { id: null }),
    };
    return new FormGroup<MegaAttributeFormGroupContent>({
      id: new FormControl(
        { value: megaAttributeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(megaAttributeRawValue.name, {
        validators: [Validators.required],
      }),
      label: new FormControl(megaAttributeRawValue.label, {
        validators: [Validators.required],
      }),
      description: new FormControl(megaAttributeRawValue.description),
      uiComponent: new FormControl(megaAttributeRawValue.uiComponent),
      type: new FormControl(megaAttributeRawValue.type, {
        validators: [Validators.required],
      }),
      required: new FormControl(megaAttributeRawValue.required),
      multiple: new FormControl(megaAttributeRawValue.multiple),
      defaultValue: new FormControl(megaAttributeRawValue.defaultValue),
      minNumber: new FormControl(megaAttributeRawValue.minNumber),
      maxNumber: new FormControl(megaAttributeRawValue.maxNumber),
      minLength: new FormControl(megaAttributeRawValue.minLength),
      maxLength: new FormControl(megaAttributeRawValue.maxLength),
      regex: new FormControl(megaAttributeRawValue.regex),
      order: new FormControl(megaAttributeRawValue.order),
      attributeGroup: new FormControl(megaAttributeRawValue.attributeGroup),
      active: new FormControl(megaAttributeRawValue.active),
      setTypes: new FormControl(megaAttributeRawValue.setTypes ?? []),
      partTypes: new FormControl(megaAttributeRawValue.partTypes ?? []),
    });
  }

  getMegaAttribute(form: MegaAttributeFormGroup): IMegaAttribute | NewMegaAttribute {
    return form.getRawValue() as IMegaAttribute | NewMegaAttribute;
  }

  resetForm(form: MegaAttributeFormGroup, megaAttribute: MegaAttributeFormGroupInput): void {
    const megaAttributeRawValue = { ...this.getFormDefaults(), ...megaAttribute };
    form.reset({
      ...megaAttributeRawValue,
      id: { value: megaAttributeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaAttributeFormDefaults {
    return {
      id: null,
      required: false,
      multiple: false,
      active: false,
      setTypes: [],
      partTypes: [],
    };
  }
}
