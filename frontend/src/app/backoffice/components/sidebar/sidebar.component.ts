import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.services';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  userName: string = '';
  userRole: string = '';

  menuItems = [
    { icon: 'work',            label: 'Projets',        link: '/admin/projects' },
    { icon: 'forum',           label: 'Forum',          link: '/admin/forum' },
    { icon: 'event',           label: 'Événements',     link: '/admin/events' },
    { icon: 'campaign',        label: 'Publicités',     link: '/admin/ads' },
    { icon: 'payments',        label: 'Transactions',   link: '/admin/transactions' },
    { icon: 'card_membership', label: 'Abonnements',   link: '/admin/subscription/list' },
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.name || user.email.split('@')[0];
      this.userRole = user.role;
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