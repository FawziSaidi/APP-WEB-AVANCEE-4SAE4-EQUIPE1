import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.services';

declare const $: any;

declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
}

export const ROUTES: RouteInfo[] = [
    { path: '/app/dashboard',           title: 'Dashboard',        icon: 'dashboard',       class: '' },
    { path: '/app/forum',               title: 'Forum',            icon: 'forum',           class: '' },
    { path: '/app/ads',                 title: 'Publicités',       icon: 'campaign',        class: '' },
    { path: '/app/transactions',        title: 'Transactions',     icon: 'payments',        class: '' },
    { path: '/app/subscription/plans',  title: 'Abonnements',      icon: 'card_membership', class: '' },
    { path: '/app/subscription/my',     title: 'Mon Abonnement',   icon: 'subscriptions',   class: '' },
    { path: '/app/subscription/ai-recommendation', title: 'AI Recommandation', icon: 'psychology', class: '' },
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  menuItems: RouteInfo[];
  userName: string = '';
  userRole: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
    
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.email.split('@')[0];
      this.userRole = user.role;
    }
  }

  isMobileMenu(): boolean {
    if ($(window).width() > 991) {
      return false;
    }
    return true;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  goToAdmin(): void {
    this.router.navigate(['/admin/dashboard']);
  }

  isAdmin(): boolean {
    return this.userRole === 'ADMIN';
  }
}