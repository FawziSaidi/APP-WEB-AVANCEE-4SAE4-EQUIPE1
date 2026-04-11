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

  menuItems = [
    { id: 'dashboard',   icon: '📊', label: 'DASHBOARD',   link: '/admin/dashboard', isLogout: false },
    { id: 'profile',     icon: '👤', label: 'PROFILE',     link: '#',               isLogout: false },
    { id: 'users',       icon: '👥', label: 'USERS TABLE', link: '#',               isLogout: false },
    { id: 'projet',      icon: '📋', label: 'PROJECT',     link: '#',               isLogout: false },
    { id: 'forum',       icon: '💬', label: 'FORUM',       link: '/admin/forum',    isLogout: false },
    { id: 'publicite',   icon: '📷', label: 'ADVERTISING', link: '#',               isLogout: false },
    { id: 'evenement',   icon: '📅', label: 'EVENTS',      link: '#',               isLogout: false },
    // ✅ FIX : isLogout=true déclenche authService.logout() dans onMenuClick()
    { id: 'deconnexion', icon: '🔌', label: 'LOGOUT',      link: '#',               isLogout: true  }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  setActiveMenu(menuId: string): void {
    this.activeMenu = menuId;
  }

  // ✅ FIX : gère la navigation ET le logout
  onMenuClick(event: Event, item: any): void {
    event.preventDefault();
    if (item.isLogout) {
      this.authService.logout();
      this.router.navigate(['/login']);
    } else {
      this.setActiveMenu(item.id);
      if (item.link !== '#') {
        this.router.navigate([item.link]);
      }
    }
  }
}