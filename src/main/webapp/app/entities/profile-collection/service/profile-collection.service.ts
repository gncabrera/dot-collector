import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfileCollection, NewProfileCollection } from '../profile-collection.model';

export type PartialUpdateProfileCollection = Partial<IProfileCollection> & Pick<IProfileCollection, 'id'>;

@Injectable()
export class ProfileCollectionsService {
  readonly profileCollectionsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly profileCollectionsResource = httpResource<IProfileCollection[]>(() => {
    const params = this.profileCollectionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of profileCollection that have been fetched. It is updated when the profileCollectionsResource emits a new value.
   * In case of error while fetching the profileCollections, the signal is set to an empty array.
   */
  readonly profileCollections = computed(() => (this.profileCollectionsResource.hasValue() ? this.profileCollectionsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/profile-collections');
}

@Injectable({ providedIn: 'root' })
export class ProfileCollectionService extends ProfileCollectionsService {
  protected readonly http = inject(HttpClient);

  create(profileCollection: NewProfileCollection): Observable<IProfileCollection> {
    return this.http.post<IProfileCollection>(this.resourceUrl, profileCollection);
  }

  update(profileCollection: IProfileCollection): Observable<IProfileCollection> {
    return this.http.put<IProfileCollection>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfileCollectionIdentifier(profileCollection))}`,
      profileCollection,
    );
  }

  partialUpdate(profileCollection: PartialUpdateProfileCollection): Observable<IProfileCollection> {
    return this.http.patch<IProfileCollection>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfileCollectionIdentifier(profileCollection))}`,
      profileCollection,
    );
  }

  find(id: number): Observable<IProfileCollection> {
    return this.http.get<IProfileCollection>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfileCollection[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfileCollection[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfileCollectionIdentifier(profileCollection: Pick<IProfileCollection, 'id'>): number {
    return profileCollection.id;
  }

  compareProfileCollection(o1: Pick<IProfileCollection, 'id'> | null, o2: Pick<IProfileCollection, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfileCollectionIdentifier(o1) === this.getProfileCollectionIdentifier(o2) : o1 === o2;
  }

  addProfileCollectionToCollectionIfMissing<Type extends Pick<IProfileCollection, 'id'>>(
    profileCollectionCollection: Type[],
    ...profileCollectionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profileCollections: Type[] = profileCollectionsToCheck.filter(isPresent);
    if (profileCollections.length > 0) {
      const profileCollectionCollectionIdentifiers = profileCollectionCollection.map(profileCollectionItem =>
        this.getProfileCollectionIdentifier(profileCollectionItem),
      );
      const profileCollectionsToAdd = profileCollections.filter(profileCollectionItem => {
        const profileCollectionIdentifier = this.getProfileCollectionIdentifier(profileCollectionItem);
        if (profileCollectionCollectionIdentifiers.includes(profileCollectionIdentifier)) {
          return false;
        }
        profileCollectionCollectionIdentifiers.push(profileCollectionIdentifier);
        return true;
      });
      return [...profileCollectionsToAdd, ...profileCollectionCollection];
    }
    return profileCollectionCollection;
  }
}
