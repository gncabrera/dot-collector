import { IMegaAttribute, NewMegaAttribute } from './mega-attribute.model';

export const sampleWithRequiredData: IMegaAttribute = {
  id: 2423,
  name: 'geez typeface enormously',
  label: 'beneath tuxedo whoa',
  type: 'BOOLEAN',
};

export const sampleWithPartialData: IMegaAttribute = {
  id: 10782,
  name: 'furthermore second',
  label: 'eek promise essay',
  description: 'off creator',
  type: 'BOOLEAN',
  required: true,
  minLength: 1005,
  order: 1627,
  attributeGroup: 'emulsify bicycle',
  active: false,
};

export const sampleWithFullData: IMegaAttribute = {
  id: 25603,
  name: 'collaborate',
  label: 'with zowie randomize',
  description: 'geez eek treble',
  uiComponent: 'DATE',
  type: 'BOOLEAN',
  required: true,
  multiple: true,
  defaultValue: 'meanwhile individual',
  minNumber: 11493.15,
  maxNumber: 31269.96,
  minLength: 4144,
  maxLength: 31993,
  regex: 'reschedule calculus knowingly',
  order: 6323,
  attributeGroup: 'sinful',
  active: false,
};

export const sampleWithNewData: NewMegaAttribute = {
  name: 'ew properly scared',
  label: 'beyond cemetery before',
  type: 'STRING',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
