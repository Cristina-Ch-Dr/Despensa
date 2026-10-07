import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';
import { Login } from './login';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let httpTestingController: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    await fixture.whenStable();
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('navigates to the app home after a successful login', () => {
    const navigateSpy = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);
    const form = fixture.nativeElement.querySelector('form') as HTMLFormElement;
    (fixture.nativeElement.querySelector('#email') as HTMLInputElement).value = 'persona@example.com';
    (fixture.nativeElement.querySelector('#password') as HTMLInputElement).value = 'password';

    form.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    httpTestingController.expectOne('/api/auth/login').flush({});

    expect(navigateSpy).toHaveBeenCalledWith('/app');
  });
});
