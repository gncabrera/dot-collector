import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaAttributeResolve from './route/mega-attribute-routing-resolve.service';

const megaAttributeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-attribute').then(m => m.MegaAttribute),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-attribute-detail').then(m => m.MegaAttributeDetail),
    resolve: {
      megaAttribute: MegaAttributeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-attribute-update').then(m => m.MegaAttributeUpdate),
    resolve: {
      megaAttribute: MegaAttributeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-attribute-update').then(m => m.MegaAttributeUpdate),
    resolve: {
      megaAttribute: MegaAttributeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaAttributeRoute;
