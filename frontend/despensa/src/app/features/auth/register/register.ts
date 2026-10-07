import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

type Campo = 'nombre' | 'apellidos' | 'email' | 'password' | 'passwordConfirmation';
type Errores = Partial<Record<Campo, string>>;

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

@Component({
  imports: [RouterLink],
  selector: 'app-register',
  styleUrl: './register.css',
  templateUrl: './register.html',
})
export class Register {
  private readonly authService = inject(AuthService);

  protected readonly isSubmitting = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly successMessage = signal('');
  protected readonly fieldErrors = signal<Errores>({});

  protected clearError(campo: Campo): void {
    if (this.fieldErrors()[campo]) {
      this.fieldErrors.update((errores) => ({ ...errores, [campo]: undefined }));
    }
  }

  private validate(datos: Record<Campo, string>): Errores {
    const errores: Errores = {};

    if (datos.nombre.length < 2) {
      errores.nombre = 'El nombre debe tener al menos 2 caracteres.';
    }

    if (datos.apellidos.length < 2) {
      errores.apellidos = 'Los apellidos deben tener al menos 2 caracteres.';
    }

    if (!EMAIL_REGEX.test(datos.email)) {
      errores.email = 'Introduce un correo electrónico válido.';
    }

    if (datos.password.length < 6) {
      errores.password = 'La contraseña debe tener al menos 6 caracteres.';
    } else if (!/\p{L}/u.test(datos.password)) {
      errores.password = 'La contraseña debe contener al menos una letra.';
    } else if (!/\d/.test(datos.password)) {
      errores.password = 'La contraseña debe contener al menos un número.';
    }

    if (datos.password !== datos.passwordConfirmation) {
      errores.passwordConfirmation = 'Las contraseñas no coinciden.';
    }

    return errores;
  }

  protected submit(event: SubmitEvent): void {
    event.preventDefault();

    const formData = new FormData(event.currentTarget as HTMLFormElement);
    const datos: Record<Campo, string> = {
      nombre: String(formData.get('nombre') ?? '').trim(),
      apellidos: String(formData.get('apellidos') ?? '').trim(),
      email: String(formData.get('email') ?? '').trim().toLowerCase(),
      password: String(formData.get('password') ?? ''), // la contraseña NO se recorta
      passwordConfirmation: String(formData.get('passwordConfirmation') ?? ''),
    };

    const errores = this.validate(datos);
    this.fieldErrors.set(errores);
    this.errorMessage.set('');
    this.successMessage.set('');

    if (Object.keys(errores).length > 0) {
      return;
    }

    this.isSubmitting.set(true);

    const { nombre, apellidos, email, password } = datos;
    this.authService.register({ nombre, apellidos, email, password }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.successMessage.set('Cuenta creada correctamente.');
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        if (err.status === 409) {
          this.fieldErrors.set({ email: 'Ya existe una cuenta con este correo.' });
        } else {
          this.errorMessage.set('No hemos podido crear la cuenta. Inténtalo de nuevo más tarde.');
        }
      },
    });
  }
}
