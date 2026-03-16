import { Routes } from '@angular/router';

export const AGENT_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'tickets',
    pathMatch: 'full',
  },
  {
    path: 'tickets',
    loadComponent: () => import('./pages/agent-tickets/agent-tickets').then((m) => m.AgentTicketsComponent),
  },
];
