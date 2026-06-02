import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: "barrier",
        loadComponent: () => import('./components/routes/auth-router/auth-router.component').then(m => m.AuthRouterComponent),
        children: [
            { path: "register", loadComponent: () => import('./components/site/register/register.component').then(m => m.RegisterComponent), pathMatch: "full" },
            { path: "register/:token", loadComponent: () => import('./components/site/register-token/register-token.component').then(m => m.RegisterTokenComponent), pathMatch: "full" },
            { path: "login", loadComponent: () => import('./components/site/login/login.component').then(m => m.LoginComponent), pathMatch: "full" },
            { path: "login/totp", loadComponent: () => import('./components/site/totp/totp.component').then(m => m.TotpComponent), pathMatch: "full" },
            { path: "logout", loadComponent: () => import('./components/site/logout/logout.component').then(m => m.LogoutComponent), pathMatch: "full" },
        ]
    },
    { path: "game/active/:uuid", loadComponent: () => import('./components/site/game-input/game-input.component').then(m => m.GameInputComponent), pathMatch: "full" },
    {
        path: "",
        loadComponent: () => import('./components/routes/main-router/main-router.component').then(m => m.MainRouterComponent),
        children: [
            { path: "", loadComponent: () => import('./components/site/home/home.component').then(m => m.HomeComponent), pathMatch: "full" },
            { path: "profile", loadComponent: () => import('./components/site/profile/profile.component').then(m => m.ProfileComponent), pathMatch: "full" },
            { path: "profile/:username", loadComponent: () => import('./components/site/profile/profile.component').then(m => m.ProfileComponent), pathMatch: "full" },
            { 
                path: "settings",
                loadComponent: () => import('./components/routes/setting-account-router/setting-account-router.component').then(m => m.SettingAccountRouterComponent),
                children: [
                    { path: "", redirectTo: "profile", pathMatch: "full" },
                    { path: "profile", loadComponent: () => import('./components/site/setting-account-profile/setting-account-profile.component').then(m => m.SettingAccountProfileComponent), pathMatch: "full" },
                    { path: "account", loadComponent: () => import('./components/site/setting-account-account/setting-account-account.component').then(m => m.SettingAccountAccountComponent), pathMatch: "full" },
                ]
            },
            { path: "group", loadComponent: () => import('./components/site/group-list/group-list.component').then(m => m.GroupListComponent), pathMatch: "full" },
            { path: "group/new", loadComponent: () => import('./components/site/group-create/group-create.component').then(m => m.GroupCreateComponent), pathMatch: "full" },
            {
                path: "group/:uuid/settings",
                loadComponent: () => import('./components/routes/setting-group-router/setting-group-router.component').then(m => m.SettingGroupRouterComponent),
                children: [
                    { path: "", redirectTo: "general", pathMatch: "full" },
                    { path: "general", loadComponent: () => import('./components/site/setting-group-general/setting-group-general.component').then(m => m.SettingGroupGeneralComponent), pathMatch: "full" },
                    { path: "members", loadComponent: () => import('./components/site/setting-group-members/setting-group-members.component').then(m => m.SettingGroupMembersComponent), pathMatch: "full" },
                    { path: "locations", loadComponent: () => import('./components/site/setting-group-locations/setting-group-locations.component').then(m => m.SettingGroupLocationsComponent), pathMatch: "full" }
                ]
            },
            { 
                path: "group/:uuid",
                loadComponent: () => import('./components/site/group/group.component').then(m => m.GroupComponent),
                children: [
                    { path: "", loadComponent: () => import('./components/site/group-overview/group-overview.component').then(m => m.GroupOverviewComponent), pathMatch: "full" },
                    { path: "games", loadComponent: () => import('./components/site/group-games/group-games.component').then(m => m.GroupGamesComponent), pathMatch: "full" },
                ]
            },
            { path: "game/new", loadComponent: () => import('./components/site/game-create/game-create.component').then(m => m.GameCreateComponent), pathMatch: "full" },
        ]
    },
    { path: "**", redirectTo: "", pathMatch: "full" }
];
