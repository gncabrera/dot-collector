import dayjs from 'dayjs/esm';

import { IMegaSetType } from 'app/entities/mega-set-type/mega-set-type.model';
import { IProfileCollectionSet } from 'app/entities/profile-collection-set/profile-collection-set.model';

export interface IMegaSet {
  id: number;
  setNumber?: string | null;
  releaseDate?: dayjs.Dayjs | null;
  notes?: string | null;
  nameEN?: string | null;
  nameES?: string | null;
  nameDE?: string | null;
  nameFR?: string | null;
  descriptionEN?: string | null;
  descriptionES?: string | null;
  descriptionDE?: string | null;
  descriptionFR?: string | null;
  attributes?: string | null;
  attributesContentType?: string | null;
  type?: Pick<IMegaSetType, 'id'> | null;
  profileCollectionSets?: Pick<IProfileCollectionSet, 'id'>[] | null;
}

export type NewMegaSet = Omit<IMegaSet, 'id'> & { id: null };
