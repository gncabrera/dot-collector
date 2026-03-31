import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PartSubCategoryResolve from './route/part-sub-category-routing-resolve.service';

const partSubCategoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/part-sub-category').then(m => m.PartSubCategory),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/part-sub-category-detail').then(m => m.PartSubCategoryDetail),
    resolve: {
      partSubCategory: PartSubCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/part-sub-category-update').then(m => m.PartSubCategoryUpdate),
    resolve: {
      partSubCategory: PartSubCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/part-sub-category-update').then(m => m.PartSubCategoryUpdate),
    resolve: {
      partSubCategory: PartSubCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default partSubCategoryRoute;
