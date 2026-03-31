import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProfileRequest, NewProfileRequest } from '../profile-request.model';

export type PartialUpdateProfileRequest = Partial<IProfileRequest> & Pick<IProfileRequest, 'id'>;

@Injectable()
export class ProfileRequestsService {
  readonly profileRequestsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly profileRequestsResource = httpResource<IProfileRequest[]>(() => {
    const params = this.profileRequestsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of profileRequest that have been fetched. It is updated when the profileRequestsResource emits a new value.
   * In case of error while fetching the profileRequests, the signal is set to an empty array.
   */
  readonly profileRequests = computed(() => (this.profileRequestsResource.hasValue() ? this.profileRequestsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/profile-requests');
}

@Injectable({ providedIn: 'root' })
export class ProfileRequestService extends ProfileRequestsService {
  protected readonly http = inject(HttpClient);

  create(profileRequest: NewProfileRequest): Observable<IProfileRequest> {
    return this.http.post<IProfileRequest>(this.resourceUrl, profileRequest);
  }

  update(profileRequest: IProfileRequest): Observable<IProfileRequest> {
    return this.http.put<IProfileRequest>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfileRequestIdentifier(profileRequest))}`,
      profileRequest,
    );
  }

  partialUpdate(profileRequest: PartialUpdateProfileRequest): Observable<IProfileRequest> {
    return this.http.patch<IProfileRequest>(
      `${this.resourceUrl}/${encodeURIComponent(this.getProfileRequestIdentifier(profileRequest))}`,
      profileRequest,
    );
  }

  find(id: number): Observable<IProfileRequest> {
    return this.http.get<IProfileRequest>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProfileRequest[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProfileRequest[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProfileRequestIdentifier(profileRequest: Pick<IProfileRequest, 'id'>): number {
    return profileRequest.id;
  }

  compareProfileRequest(o1: Pick<IProfileRequest, 'id'> | null, o2: Pick<IProfileRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfileRequestIdentifier(o1) === this.getProfileRequestIdentifier(o2) : o1 === o2;
  }

  addProfileRequestToCollectionIfMissing<Type extends Pick<IProfileRequest, 'id'>>(
    profileRequestCollection: Type[],
    ...profileRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profileRequests: Type[] = profileRequestsToCheck.filter(isPresent);
    if (profileRequests.length > 0) {
      const profileRequestCollectionIdentifiers = profileRequestCollection.map(profileRequestItem =>
        this.getProfileRequestIdentifier(profileRequestItem),
      );
      const profileRequestsToAdd = profileRequests.filter(profileRequestItem => {
        const profileRequestIdentifier = this.getProfileRequestIdentifier(profileRequestItem);
        if (profileRequestCollectionIdentifiers.includes(profileRequestIdentifier)) {
          return false;
        }
        profileRequestCollectionIdentifiers.push(profileRequestIdentifier);
        return true;
      });
      return [...profileRequestsToAdd, ...profileRequestCollection];
    }
    return profileRequestCollection;
  }
}
