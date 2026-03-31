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
import { MegaSetPartCountDeleteDialog } from '../delete/mega-set-part-count-delete-dialog';
import { IMegaSetPartCount } from '../mega-set-part-count.model';
import { MegaSetPartCountService } from '../service/mega-set-part-count.service';

@Component({
  selector: 'jhi-mega-set-part-count',
  templateUrl: './mega-set-part-count.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class MegaSetPartCount implements OnInit {
  subscription: Subscription | null = null;
  readonly megaSetPartCounts = signal<IMegaSetPartCount[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly megaSetPartCountService = inject(MegaSetPartCountService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.megaSetPartCountService.megaSetPartCountsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.megaSetPartCounts.set(this.fillComponentAttributesFromResponseBody([...this.megaSetPartCountService.megaSetPartCounts()]));
    });
  }

  trackId = (item: IMegaSetPartCount): number => this.megaSetPartCountService.getMegaSetPartCountIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.megaSetPartCounts().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(megaSetPartCount: IMegaSetPartCount): void {
    const modalRef = this.modalService.open(MegaSetPartCountDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.megaSetPartCount = megaSetPartCount;
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

  protected refineData(data: IMegaSetPartCount[]): IMegaSetPartCount[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IMegaSetPartCount[]): IMegaSetPartCount[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.megaSetPartCountService.megaSetPartCountsParams.set(queryObject);
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
