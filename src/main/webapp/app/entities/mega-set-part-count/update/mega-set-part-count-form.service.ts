import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMegaSetPartCount, NewMegaSetPartCount } from '../mega-set-part-count.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMegaSetPartCount for edit and NewMegaSetPartCountFormGroupInput for create.
 */
type MegaSetPartCountFormGroupInput = IMegaSetPartCount | PartialWithRequiredKeyOf<NewMegaSetPartCount>;

type MegaSetPartCountFormDefaults = Pick<NewMegaSetPartCount, 'id'>;

type MegaSetPartCountFormGroupContent = {
  id: FormControl<IMegaSetPartCount['id'] | NewMegaSetPartCount['id']>;
  count: FormControl<IMegaSetPartCount['count']>;
  set: FormControl<IMegaSetPartCount['set']>;
  part: FormControl<IMegaSetPartCount['part']>;
};

export type MegaSetPartCountFormGroup = FormGroup<MegaSetPartCountFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MegaSetPartCountFormService {
  createMegaSetPartCountFormGroup(megaSetPartCount?: MegaSetPartCountFormGroupInput): MegaSetPartCountFormGroup {
    const megaSetPartCountRawValue = {
      ...this.getFormDefaults(),
      ...(megaSetPartCount ?? { id: null }),
    };
    return new FormGroup<MegaSetPartCountFormGroupContent>({
      id: new FormControl(
        { value: megaSetPartCountRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      count: new FormControl(megaSetPartCountRawValue.count),
      set: new FormControl(megaSetPartCountRawValue.set),
      part: new FormControl(megaSetPartCountRawValue.part),
    });
  }

  getMegaSetPartCount(form: MegaSetPartCountFormGroup): IMegaSetPartCount | NewMegaSetPartCount {
    return form.getRawValue() as IMegaSetPartCount | NewMegaSetPartCount;
  }

  resetForm(form: MegaSetPartCountFormGroup, megaSetPartCount: MegaSetPartCountFormGroupInput): void {
    const megaSetPartCountRawValue = { ...this.getFormDefaults(), ...megaSetPartCount };
    form.reset({
      ...megaSetPartCountRawValue,
      id: { value: megaSetPartCountRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): MegaSetPartCountFormDefaults {
    return {
      id: null,
    };
  }
}
