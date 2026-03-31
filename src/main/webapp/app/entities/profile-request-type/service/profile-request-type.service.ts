import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfileRequestType, NewProfileRequestType } from '../profile-request-type.model';

export type PartialUpdateProfileRequestType = Partial<IProfileRequestType> & Pick<IProfileRequestType, 'id'>;

@Injectable()
export class ProfileRequestTypesService {
  readonly profileRequestTypesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly profileRequestTypesResource = httpResource<IProfileRequestType[]>(() => {
    const params = this.profileRequestTypesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of profileRequestType that have been fetched. It is updated when the profileRequestTypesResource emits a new value.
   * In case of error while fetching the profileRequestTypes, the signal is set to an empty array.
   */
  readonly profileRequestTypes = computed(() =>
    this.profileRequestTypesResource.hasValue() ? this.profileRequestTypesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/profile-request-types');
}

@Injectable({ providedIn: 'root' })
export class ProfileRequestTypeService extends ProfileRequestTypesService {
  protected readonly http = inject(HttpClient);

  create(profileRequestType: NewProfileRequestType): Observable<IProfileRequestType> {
    return this.http.post<IProfileRequestType>(this.resourceUrl, profileRequestType);
  }

  update(profileRequestType: IProfileRequestType): Observable<IProfileRequestType> {
    return this.http.put<IProfileRequestType>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfileRequestTypeIdentifier(profileRequestType))}`,
      profileRequestType,
    );
  }

  partialUpdate(profileRequestType: PartialUpdateProfileRequestType): Observable<IProfileRequestType> {
    return this.http.patch<IProfileRequestType>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfileRequestTypeIdentifier(profileRequestType))}`,
      profileRequestType,
    );
  }

  find(id: number): Observable<IProfileRequestType> {
    return this.http.get<IProfileRequestType>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfileRequestType[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfileRequestType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfileRequestTypeIdentifier(profileRequestType: Pick<IProfileRequestType, 'id'>): number {
    return profileRequestType.id;
  }

  compareProfileRequestType(o1: Pick<IProfileRequestType, 'id'> | null, o2: Pick<IProfileRequestType, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfileRequestTypeIdentifier(o1) === this.getProfileRequestTypeIdentifier(o2) : o1 === o2;
  }

  addProfileRequestTypeToCollectionIfMissing<Type extends Pick<IProfileRequestType, 'id'>>(
    profileRequestTypeCollection: Type[],
    ...profileRequestTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profileRequestTypes: Type[] = profileRequestTypesToCheck.filter(isPresent);
    if (profileRequestTypes.length > 0) {
      const profileRequestTypeCollectionIdentifiers = profileRequestTypeCollection.map(profileRequestTypeItem =>
        this.getProfileRequestTypeIdentifier(profileRequestTypeItem),
      );
      const profileRequestTypesToAdd = profileRequestTypes.filter(profileRequestTypeItem => {
        const profileRequestTypeIdentifier = this.getProfileRequestTypeIdentifier(profileRequestTypeItem);
        if (profileRequestTypeCollectionIdentifiers.includes(profileRequestTypeIdentifier)) {
          return false;
        }
        profileRequestTypeCollectionIdentifiers.push(profileRequestTypeIdentifier);
        return true;
      });
      return [...profileRequestTypesToAdd, ...profileRequestTypeCollection];
    }
    return profileRequestTypeCollection;
  }
}
