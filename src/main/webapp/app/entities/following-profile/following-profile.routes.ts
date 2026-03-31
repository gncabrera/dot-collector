import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import FollowingProfileResolve from './route/following-profile-routing-resolve.service';

const followingProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/following-profile').then(m => m.FollowingProfile),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/following-profile-detail').then(m => m.FollowingProfileDetail),
    resolve: {
      followingProfile: FollowingProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/following-profile-update').then(m => m.FollowingProfileUpdate),
    resolve: {
      followingProfile: FollowingProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/following-profile-update').then(m => m.FollowingProfileUpdate),
    resolve: {
      followingProfile: FollowingProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default followingProfileRoute;
