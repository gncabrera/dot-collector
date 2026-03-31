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
import { PartCategoryDeleteDialog } from '../delete/part-category-delete-dialog';
import { IPartCategory } from '../part-category.model';
import { PartCategoryService } from '../service/part-category.service';

@Component({
  selector: 'jhi-part-category',
  templateUrl: './part-category.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class PartCategory implements OnInit {
  subscription: Subscription | null = null;
  readonly partCategories = signal<IPartCategory[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly partCategoryService = inject(PartCategoryService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.partCategoryService.partCategoriesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.partCategories.set(this.fillComponentAttributesFromResponseBody([...this.partCategoryService.partCategories()]));
    });
  }

  trackId = (item: IPartCategory): number => this.partCategoryService.getPartCategoryIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.partCategories().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(partCategory: IPartCategory): void {
    const modalRef = this.modalService.open(PartCategoryDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.partCategory = partCategory;
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

  protected refineData(data: IPartCategory[]): IPartCategory[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IPartCategory[]): IPartCategory[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.partCategoryService.partCategoriesParams.set(queryObject);
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
