import { IProfile, NewProfile } from './profile.model';

export const sampleWithRequiredData: IProfile = {
  id: 18889,
};

export const sampleWithPartialData: IProfile = {
  id: 23204,
  username: 'skateboard below quietly',
  fullName: 'dowse amongst',
};

export const sampleWithFullData: IProfile = {
  id: 29727,
  username: 'shoulder',
  fullName: 'pulse sticker',
};

export const sampleWithNewData: NewProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
