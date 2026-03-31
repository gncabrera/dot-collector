import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { MegaPartSubPartCountDeleteDialog } from '../delete/mega-part-sub-part-count-delete-dialog';
import { IMegaPartSubPartCount } from '../mega-part-sub-part-count.model';
import { MegaPartSubPartCountService } from '../service/mega-part-sub-part-count.service';

@Component({
  selector: 'jhi-mega-part-sub-part-count',
  templateUrl: './mega-part-sub-part-count.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class MegaPartSubPartCount implements OnInit {
  subscription: Subscription | null = null;
  readonly megaPartSubPartCounts = signal<IMegaPartSubPartCount[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly megaPartSubPartCountService = inject(MegaPartSubPartCountService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.megaPartSubPartCountService.megaPartSubPartCountsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.megaPartSubPartCounts.set(
        this.fillComponentAttributesFromResponseBody([...this.megaPartSubPartCountService.megaPartSubPartCounts()]),
      );
    });
  }

  trackId = (item: IMegaPartSubPartCount): number => this.megaPartSubPartCountService.getMegaPartSubPartCountIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.megaPartSubPartCounts().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(megaPartSubPartCount: IMegaPartSubPartCount): void {
    const modalRef = this.modalService.open(MegaPartSubPartCountDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.megaPartSubPartCount = megaPartSubPartCount;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IMegaPartSubPartCount[]): IMegaPartSubPartCount[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IMegaPartSubPartCount[]): IMegaPartSubPartCount[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.megaPartSubPartCountService.megaPartSubPartCountsParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
