import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  imports: [RouterLink],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly showPassword = signal(false);
  protected readonly isSubmitting = signal(false);
  protected readonly errorMessage = signal('');

  protected togglePassword(): void {
    this.showPassword.update((visible) => !visible);
  }

  protected submit(event: SubmitEvent): void {
    event.preventDefault();

    const form = event.currentTarget as HTMLFormElement;
    const formData = new FormData(form);
    const email = String(formData.get('email') ?? '');
    const password = String(formData.get('password') ?? '');

    this.isSubmitting.set(true);
    this.errorMessage.set('');

    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        void this.router.navigateByUrl('/app');
      },
      error: () => {
        this.isSubmitting.set(false);
        this.errorMessage.set('No hemos podido iniciar sesión. Revisa tus datos e inténtalo de nuevo.');
      },
    });
  }
}
