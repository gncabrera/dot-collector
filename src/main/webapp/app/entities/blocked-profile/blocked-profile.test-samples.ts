import dayjs from 'dayjs/esm';

import { IBlockedProfile, NewBlockedProfile } from './blocked-profile.model';

export const sampleWithRequiredData: IBlockedProfile = {
  id: 9614,
};

export const sampleWithPartialData: IBlockedProfile = {
  id: 6656,
  reason: 'lest plus',
};

export const sampleWithFullData: IBlockedProfile = {
  id: 25497,
  reason: 'so dull amongst',
  dateBlocked: dayjs('2026-03-30'),
};

export const sampleWithNewData: NewBlockedProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
