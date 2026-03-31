import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaPartSubPartCount, NewMegaPartSubPartCount } from '../mega-part-sub-part-count.model';

export type PartialUpdateMegaPartSubPartCount = Partial<IMegaPartSubPartCount> & Pick<IMegaPartSubPartCount, 'id'>;

@Injectable()
export class MegaPartSubPartCountsService {
  readonly megaPartSubPartCountsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly megaPartSubPartCountsResource = httpResource<IMegaPartSubPartCount[]>(() => {
    const params = this.megaPartSubPartCountsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaPartSubPartCount that have been fetched. It is updated when the megaPartSubPartCountsResource emits a new value.
   * In case of error while fetching the megaPartSubPartCounts, the signal is set to an empty array.
   */
  readonly megaPartSubPartCounts = computed(() =>
    this.megaPartSubPartCountsResource.hasValue() ? this.megaPartSubPartCountsResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-part-sub-part-counts');
}

@Injectable({ providedIn: 'root' })
export class MegaPartSubPartCountService extends MegaPartSubPartCountsService {
  protected readonly http = inject(HttpClient);

  create(megaPartSubPartCount: NewMegaPartSubPartCount): Observable<IMegaPartSubPartCount> {
    return this.http.post<IMegaPartSubPartCount>(this.resourceUrl, megaPartSubPartCount);
  }

  update(megaPartSubPartCount: IMegaPartSubPartCount): Observable<IMegaPartSubPartCount> {
    return this.http.put<IMegaPartSubPartCount>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaPartSubPartCountIdentifier(megaPartSubPartCount))}`,
      megaPartSubPartCount,
    );
  }

  partialUpdate(megaPartSubPartCount: PartialUpdateMegaPartSubPartCount): Observable<IMegaPartSubPartCount> {
    return this.http.patch<IMegaPartSubPartCount>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaPartSubPartCountIdentifier(megaPartSubPartCount))}`,
      megaPartSubPartCount,
    );
  }

  find(id: number): Observable<IMegaPartSubPartCount> {
    return this.http.get<IMegaPartSubPartCount>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMegaPartSubPartCount[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMegaPartSubPartCount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaPartSubPartCountIdentifier(megaPartSubPartCount: Pick<IMegaPartSubPartCount, 'id'>): number {
    return megaPartSubPartCount.id;
  }

  compareMegaPartSubPartCount(o1: Pick<IMegaPartSubPartCount, 'id'> | null, o2: Pick<IMegaPartSubPartCount, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaPartSubPartCountIdentifier(o1) === this.getMegaPartSubPartCountIdentifier(o2) : o1 === o2;
  }

  addMegaPartSubPartCountToCollectionIfMissing<Type extends Pick<IMegaPartSubPartCount, 'id'>>(
    megaPartSubPartCountCollection: Type[],
    ...megaPartSubPartCountsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaPartSubPartCounts: Type[] = megaPartSubPartCountsToCheck.filter(isPresent);
    if (megaPartSubPartCounts.length > 0) {
      const megaPartSubPartCountCollectionIdentifiers = megaPartSubPartCountCollection.map(megaPartSubPartCountItem =>
        this.getMegaPartSubPartCountIdentifier(megaPartSubPartCountItem),
      );
      const megaPartSubPartCountsToAdd = megaPartSubPartCounts.filter(megaPartSubPartCountItem => {
        const megaPartSubPartCountIdentifier = this.getMegaPartSubPartCountIdentifier(megaPartSubPartCountItem);
        if (megaPartSubPartCountCollectionIdentifiers.includes(megaPartSubPartCountIdentifier)) {
          return false;
        }
        megaPartSubPartCountCollectionIdentifiers.push(megaPartSubPartCountIdentifier);
        return true;
      });
      return [...megaPartSubPartCountsToAdd, ...megaPartSubPartCountCollection];
    }
    return megaPartSubPartCountCollection;
  }
}
