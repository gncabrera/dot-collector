import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaAttributeOptionResolve from './route/mega-attribute-option-routing-resolve.service';

const megaAttributeOptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-attribute-option').then(m => m.MegaAttributeOption),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-attribute-option-detail').then(m => m.MegaAttributeOptionDetail),
    resolve: {
      megaAttributeOption: MegaAttributeOptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-attribute-option-update').then(m => m.MegaAttributeOptionUpdate),
    resolve: {
      megaAttributeOption: MegaAttributeOptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-attribute-option-update').then(m => m.MegaAttributeOptionUpdate),
    resolve: {
      megaAttributeOption: MegaAttributeOptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaAttributeOptionRoute;
