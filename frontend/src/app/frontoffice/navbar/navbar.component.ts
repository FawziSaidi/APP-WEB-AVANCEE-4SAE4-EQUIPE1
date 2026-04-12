import { Component, OnInit, ElementRef } from '@angular/core';
import { ROUTES } from '../sidebar/sidebar.component';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.services';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
    private listTitles: any[];
    location: Location;
    mobile_menu_visible: any = 0;
    private toggleButton: any;
    private sidebarVisible: boolean;
    userName: string = '';
    userRole: string = '';

    constructor(
      location: Location,
      private element: ElementRef,
      private router: Router,
      private authService: AuthService
    ) {
      this.location = location;
      this.sidebarVisible = false;
    }

    ngOnInit() {
      this.listTitles = ROUTES.filter(listTitle => listTitle);
      const navbar: HTMLElement = this.element.nativeElement;
      this.toggleButton = navbar.getElementsByClassName('navbar-toggler')[0];
      this.router.events.subscribe((event) => {
        this.sidebarClose();
        const layer: any = document.getElementsByClassName('close-layer')[0];
        if (layer) {
          layer.remove();
          this.mobile_menu_visible = 0;
        }
      });

      const user = this.authService.getCurrentUser();
      if (user) {
        this.userName = user.email.split('@')[0];
        this.userRole = user.role;
      }
    }

    logout(): void {
      this.authService.logout();
      this.router.navigate(['/login']);
    }

    sidebarOpen() {
      const toggleButton = this.toggleButton;
      const body = document.getElementsByTagName('body')[0];
      setTimeout(function() {
        toggleButton.classList.add('toggled');
      }, 500);
      body.classList.add('nav-open');
      this.sidebarVisible = true;
    }

    sidebarClose() {
      const body = document.getElementsByTagName('body')[0];
      this.toggleButton.classList.remove('toggled');
      this.sidebarVisible = false;
      body.classList.remove('nav-open');
    }

    sidebarToggle() {
      const toggle = document.getElementsByClassName('navbar-toggler')[0];

      if (this.sidebarVisible === false) {
        this.sidebarOpen();
      } else {
        this.sidebarClose();
      }
      const body = document.getElementsByTagName('body')[0];

      if (this.mobile_menu_visible == 1) {
        body.classList.remove('nav-open');
        const existingLayer: any = document.getElementsByClassName('close-layer')[0];
        if (existingLayer) {
          existingLayer.remove();
        }
        setTimeout(function() {
          toggle.classList.remove('toggled');
        }, 400);
        this.mobile_menu_visible = 0;
      } else {
        setTimeout(function() {
          toggle.classList.add('toggled');
        }, 430);

        const newLayer = document.createElement('div');
        newLayer.setAttribute('class', 'close-layer');

        if (body.querySelectorAll('.main-panel')) {
          document.getElementsByClassName('main-panel')[0].appendChild(newLayer);
        } else if (body.classList.contains('off-canvas-sidebar')) {
          document.getElementsByClassName('wrapper-full-page')[0].appendChild(newLayer);
        }

        setTimeout(function() {
          newLayer.classList.add('visible');
        }, 100);

        newLayer.onclick = () => {
          body.classList.remove('nav-open');
          this.mobile_menu_visible = 0;
          newLayer.classList.remove('visible');
          setTimeout(function() {
            newLayer.remove();
            toggle.classList.remove('toggled');
          }, 400);
        };

        body.classList.add('nav-open');
        this.mobile_menu_visible = 1;
      }
    }

    getTitle() {
      let titlee = this.location.prepareExternalUrl(this.location.path());
      if (titlee.charAt(0) === '#') {
        titlee = titlee.slice(1);
      }

      for (let item = 0; item < this.listTitles.length; item++) {
        if (this.listTitles[item].path === titlee) {
          return this.listTitles[item].title;
        }
      }
      return 'Dashboard';
    }
}