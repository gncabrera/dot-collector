import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaSetPartCount, NewMegaSetPartCount } from '../mega-set-part-count.model';

export type PartialUpdateMegaSetPartCount = Partial<IMegaSetPartCount> & Pick<IMegaSetPartCount, 'id'>;

@Injectable()
export class MegaSetPartCountsService {
  readonly megaSetPartCountsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly megaSetPartCountsResource = httpResource<IMegaSetPartCount[]>(() => {
    const params = this.megaSetPartCountsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaSetPartCount that have been fetched. It is updated when the megaSetPartCountsResource emits a new value.
   * In case of error while fetching the megaSetPartCounts, the signal is set to an empty array.
   */
  readonly megaSetPartCounts = computed(() => (this.megaSetPartCountsResource.hasValue() ? this.megaSetPartCountsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-set-part-counts');
}

@Injectable({ providedIn: 'root' })
export class MegaSetPartCountService extends MegaSetPartCountsService {
  protected readonly http = inject(HttpClient);

  create(megaSetPartCount: NewMegaSetPartCount): Observable<IMegaSetPartCount> {
    return this.http.post<IMegaSetPartCount>(this.resourceUrl, megaSetPartCount);
  }

  update(megaSetPartCount: IMegaSetPartCount): Observable<IMegaSetPartCount> {
    return this.http.put<IMegaSetPartCount>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaSetPartCountIdentifier(megaSetPartCount))}`,
      megaSetPartCount,
    );
  }

  partialUpdate(megaSetPartCount: PartialUpdateMegaSetPartCount): Observable<IMegaSetPartCount> {
    return this.http.patch<IMegaSetPartCount>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaSetPartCountIdentifier(megaSetPartCount))}`,
      megaSetPartCount,
    );
  }

  find(id: number): Observable<IMegaSetPartCount> {
    return this.http.get<IMegaSetPartCount>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMegaSetPartCount[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMegaSetPartCount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaSetPartCountIdentifier(megaSetPartCount: Pick<IMegaSetPartCount, 'id'>): number {
    return megaSetPartCount.id;
  }

  compareMegaSetPartCount(o1: Pick<IMegaSetPartCount, 'id'> | null, o2: Pick<IMegaSetPartCount, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaSetPartCountIdentifier(o1) === this.getMegaSetPartCountIdentifier(o2) : o1 === o2;
  }

  addMegaSetPartCountToCollectionIfMissing<Type extends Pick<IMegaSetPartCount, 'id'>>(
    megaSetPartCountCollection: Type[],
    ...megaSetPartCountsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaSetPartCounts: Type[] = megaSetPartCountsToCheck.filter(isPresent);
    if (megaSetPartCounts.length > 0) {
      const megaSetPartCountCollectionIdentifiers = megaSetPartCountCollection.map(megaSetPartCountItem =>
        this.getMegaSetPartCountIdentifier(megaSetPartCountItem),
      );
      const megaSetPartCountsToAdd = megaSetPartCounts.filter(megaSetPartCountItem => {
        const megaSetPartCountIdentifier = this.getMegaSetPartCountIdentifier(megaSetPartCountItem);
        if (megaSetPartCountCollectionIdentifiers.includes(megaSetPartCountIdentifier)) {
          return false;
        }
        megaSetPartCountCollectionIdentifiers.push(megaSetPartCountIdentifier);
        return true;
      });
      return [...megaSetPartCountsToAdd, ...megaSetPartCountCollection];
    }
    return megaSetPartCountCollection;
  }
}
