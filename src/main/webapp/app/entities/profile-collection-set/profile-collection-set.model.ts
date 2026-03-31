import dayjs from 'dayjs/esm';

import { IMegaSet } from 'app/entities/mega-set/mega-set.model';
import { IProfileCollection } from 'app/entities/profile-collection/profile-collection.model';

export interface IProfileCollectionSet {
  id: number;
  owned?: boolean | null;
  wanted?: boolean | null;
  dateAdded?: dayjs.Dayjs | null;
  collection?: Pick<IProfileCollection, 'id'> | null;
  sets?: Pick<IMegaSet, 'id'>[] | null;
}

export type NewProfileCollectionSet = Omit<IProfileCollectionSet, 'id'> & { id: null };
