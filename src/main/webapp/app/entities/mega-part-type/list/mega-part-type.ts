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
import { MegaPartTypeDeleteDialog } from '../delete/mega-part-type-delete-dialog';
import { IMegaPartType } from '../mega-part-type.model';
import { MegaPartTypeService } from '../service/mega-part-type.service';

@Component({
  selector: 'jhi-mega-part-type',
  templateUrl: './mega-part-type.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class MegaPartType implements OnInit {
  subscription: Subscription | null = null;
  readonly megaPartTypes = signal<IMegaPartType[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly megaPartTypeService = inject(MegaPartTypeService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.megaPartTypeService.megaPartTypesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.megaPartTypes.set(this.fillComponentAttributesFromResponseBody([...this.megaPartTypeService.megaPartTypes()]));
    });
  }

  trackId = (item: IMegaPartType): number => this.megaPartTypeService.getMegaPartTypeIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.megaPartTypes().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(megaPartType: IMegaPartType): void {
    const modalRef = this.modalService.open(MegaPartTypeDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.megaPartType = megaPartType;
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

  protected refineData(data: IMegaPartType[]): IMegaPartType[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IMegaPartType[]): IMegaPartType[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.megaPartTypeService.megaPartTypesParams.set(queryObject);
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
