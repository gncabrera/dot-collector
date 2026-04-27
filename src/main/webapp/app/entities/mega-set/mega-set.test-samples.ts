import dayjs from 'dayjs/esm';

import { IMegaSet, NewMegaSet } from './mega-set.model';

export const sampleWithRequiredData: IMegaSet = {
  id: 11613,
  setNumber: 'sympathetically',
  name: 'esteemed',
  description: 'aboard',
};

export const sampleWithPartialData: IMegaSet = {
  id: 6787,
  setNumber: 'gulp bide',
  releaseDate: dayjs('2026-03-31'),
  name: 'gee',
  description: 'precious',
  attributes: null,
  attributesContentType: null,
};

export const sampleWithFullData: IMegaSet = {
  id: 14614,
  setNumber: 'pfft extract but',
  releaseDate: dayjs('2026-03-31'),
  notes: 'trusting yum above',
  name: 'for jubilantly rekindle',
  description: 'youthfully french',
  attributes: null,
  attributesContentType: null,
};

export const sampleWithNewData: NewMegaSet = {
  setNumber: 'mostly prance defrag',
  name: 'vast tedious austere',
  description: 'quarrel garage bookcase',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
