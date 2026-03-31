import dayjs from 'dayjs/esm';

import { IFollowingProfile, NewFollowingProfile } from './following-profile.model';

export const sampleWithRequiredData: IFollowingProfile = {
  id: 5457,
};

export const sampleWithPartialData: IFollowingProfile = {
  id: 16318,
};

export const sampleWithFullData: IFollowingProfile = {
  id: 4351,
  dateFollowing: dayjs('2026-03-31'),
};

export const sampleWithNewData: NewFollowingProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
