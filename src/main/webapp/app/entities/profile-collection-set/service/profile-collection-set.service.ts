import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfileCollectionSet, NewProfileCollectionSet } from '../profile-collection-set.model';

export type PartialUpdateProfileCollectionSet = Partial<IProfileCollectionSet> & Pick<IProfileCollectionSet, 'id'>;

type RestOf<T extends IProfileCollectionSet | NewProfileCollectionSet> = Omit<T, 'dateAdded'> & {
  dateAdded?: string | null;
};

export type RestProfileCollectionSet = RestOf<IProfileCollectionSet>;

export type NewRestProfileCollectionSet = RestOf<NewProfileCollectionSet>;

export type PartialUpdateRestProfileCollectionSet = RestOf<PartialUpdateProfileCollectionSet>;

@Injectable()
export class ProfileCollectionSetsService {
  readonly profileCollectionSetsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly profileCollectionSetsResource = httpResource<RestProfileCollectionSet[]>(() => {
    const params = this.profileCollectionSetsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of profileCollectionSet that have been fetched. It is updated when the profileCollectionSetsResource emits a new value.
   * In case of error while fetching the profileCollectionSets, the signal is set to an empty array.
   */
  readonly profileCollectionSets = computed(() =>
    (this.profileCollectionSetsResource.hasValue() ? this.profileCollectionSetsResource.value() : []).map(item =>
      this.convertValueFromServer(item),
    ),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/profile-collection-sets');

  protected convertValueFromServer(restProfileCollectionSet: RestProfileCollectionSet): IProfileCollectionSet {
    return {
      ...restProfileCollectionSet,
      dateAdded: restProfileCollectionSet.dateAdded ? dayjs(restProfileCollectionSet.dateAdded) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ProfileCollectionSetService extends ProfileCollectionSetsService {
  protected readonly http = inject(HttpClient);

  create(profileCollectionSet: NewProfileCollectionSet): Observable<IProfileCollectionSet> {
    const copy = this.convertValueFromClient(profileCollectionSet);
    return this.http.post<RestProfileCollectionSet>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(profileCollectionSet: IProfileCollectionSet): Observable<IProfileCollectionSet> {
    const copy = this.convertValueFromClient(profileCollectionSet);
    return this.http
      .put<RestProfileCollectionSet>(
        `${this.resourceUrl}/${encodeURIComponent(this.getProfileCollectionSetIdentifier(profileCollectionSet))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(profileCollectionSet: PartialUpdateProfileCollectionSet): Observable<IProfileCollectionSet> {
    const copy = this.convertValueFromClient(profileCollectionSet);
    return this.http
      .patch<RestProfileCollectionSet>(
        `${this.resourceUrl}/${encodeURIComponent(this.getProfileCollectionSetIdentifier(profileCollectionSet))}`,
        copy,
      )
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IProfileCollectionSet> {
    return this.http
      .get<RestProfileCollectionSet>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IProfileCollectionSet[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProfileCollectionSet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfileCollectionSetIdentifier(profileCollectionSet: Pick<IProfileCollectionSet, 'id'>): number {
    return profileCollectionSet.id;
  }

  compareProfileCollectionSet(o1: Pick<IProfileCollectionSet, 'id'> | null, o2: Pick<IProfileCollectionSet, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfileCollectionSetIdentifier(o1) === this.getProfileCollectionSetIdentifier(o2) : o1 === o2;
  }

  addProfileCollectionSetToCollectionIfMissing<Type extends Pick<IProfileCollectionSet, 'id'>>(
    profileCollectionSetCollection: Type[],
    ...profileCollectionSetsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profileCollectionSets: Type[] = profileCollectionSetsToCheck.filter(isPresent);
    if (profileCollectionSets.length > 0) {
      const profileCollectionSetCollectionIdentifiers = profileCollectionSetCollection.map(profileCollectionSetItem =>
        this.getProfileCollectionSetIdentifier(profileCollectionSetItem),
      );
      const profileCollectionSetsToAdd = profileCollectionSets.filter(profileCollectionSetItem => {
        const profileCollectionSetIdentifier = this.getProfileCollectionSetIdentifier(profileCollectionSetItem);
        if (profileCollectionSetCollectionIdentifiers.includes(profileCollectionSetIdentifier)) {
          return false;
        }
        profileCollectionSetCollectionIdentifiers.push(profileCollectionSetIdentifier);
        return true;
      });
      return [...profileCollectionSetsToAdd, ...profileCollectionSetCollection];
    }
    return profileCollectionSetCollection;
  }

  protected convertValueFromClient<T extends IProfileCollectionSet | NewProfileCollectionSet | PartialUpdateProfileCollectionSet>(
    profileCollectionSet: T,
  ): RestOf<T> {
    return {
      ...profileCollectionSet,
      dateAdded: profileCollectionSet.dateAdded?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestProfileCollectionSet): IProfileCollectionSet {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestProfileCollectionSet[]): IProfileCollectionSet[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
