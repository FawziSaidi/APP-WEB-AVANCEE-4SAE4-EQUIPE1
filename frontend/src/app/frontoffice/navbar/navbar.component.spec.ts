import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing'; // ← Fixed import
import { RouterTestingModule } from '@angular/router/testing'; // ← Add this
import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../services/auth.services';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(waitForAsync(() => { // ← Use waitForAsync instead of async
    TestBed.configureTestingModule({
      declarations: [ NavbarComponent ],
      imports: [ RouterTestingModule ], // ← Add this
      providers: [ AuthService ] // ← Add this if needed
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});