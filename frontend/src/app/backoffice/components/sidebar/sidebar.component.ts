import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.services';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  activeMenu: string = 'dashboard';
  userName: string = '';
  userRole: string = '';

  menuItems = [
    { id: 'dashboard',     icon: 'dashboard',        label: 'Dashboard',         link: '/admin/dashboard' },
    { id: 'users',         icon: 'people',           label: 'Utilisateurs',      link: '/admin/users' },
    { id: 'forum',         icon: 'forum',            label: 'Forum',             link: '/admin/forum' },
    { id: 'ads',           icon: 'campaign',         label: 'Publicités',        link: '/admin/ads' },
    { id: 'transactions',  icon: 'payments',         label: 'Transactions',      link: '/admin/transactions' },
  ];

  subscriptionItems = [
    { id: 'sub-list',   icon: 'list',           label: 'Liste Abonnements', link: '/admin/subscription/list' },
    { id: 'sub-create', icon: 'add_circle',     label: 'Créer Abonnement',  link: '/admin/subscription/create' },
    { id: 'sub-stats',  icon: 'bar_chart',      label: 'Statistiques',      link: '/admin/subscription/stats' },
    { id: 'sub-churn',  icon: 'trending_down',  label: 'Churn Prediction',  link: '/admin/subscription/churn' },
    { id: 'sub-promos', icon: 'local_offer',    label: 'Promotions',        link: '/admin/subscription/promos' },
  ];

  subscriptionMenuOpen: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.email.split('@')[0];
      this.userRole = user.role;
    }
  }

  setActiveMenu(menuId: string): void {
    this.activeMenu = menuId;
  }

  toggleSubscriptionMenu(): void {
    this.subscriptionMenuOpen = !this.subscriptionMenuOpen;
  }

  onMenuClick(event: Event, item: any): void {
    event.preventDefault();
    this.setActiveMenu(item.id);
    if (item.link && item.link !== '#') {
      this.router.navigate([item.link]);
    }
  }

  goToFrontoffice(): void {
    this.router.navigate(['/app/dashboard']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}