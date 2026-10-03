import { ListaResumen } from "./lista";
import { UsuarioResumen } from "./usuario";

export interface GrupoDto {
    grupoId: number;
    nombre: string;
    descripcion?: string;
    miembros?: UsuarioResumen[];
    listas?: ListaResumen[];     // resumen de listas
}

export interface GrupoResumen {
    grupoId: number;
    nombre: string;
}