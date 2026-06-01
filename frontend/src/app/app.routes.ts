import { Routes } from '@angular/router';
import { AuthRouterComponent } from './components/routes/auth-router/auth-router.component';
import { LoginComponent } from './components/site/login/login.component';
import { TotpComponent } from './components/site/totp/totp.component';
import { MainRouterComponent } from './components/routes/main-router/main-router.component';
import { HomeComponent } from './components/site/home/home.component';
import { LogoutComponent } from './components/site/logout/logout.component';
import { ProfileComponent } from './components/site/profile/profile.component';
import { SettingAccountRouterComponent } from './components/routes/setting-account-router/setting-account-router.component';
import { SettingAccountProfileComponent } from './components/site/setting-account-profile/setting-account-profile.component';
import { SettingAccountAccountComponent } from './components/site/setting-account-account/setting-account-account.component';
import { GameInputComponent } from './components/site/game-input/game-input.component';
import { GroupListComponent } from './components/site/group-list/group-list.component';
import { RegisterComponent } from './components/site/register/register.component';
import { GroupComponent } from './components/site/group/group.component';
import { GroupOverviewComponent } from './components/site/group-overview/group-overview.component';
import { GroupGamesComponent } from './components/site/group-games/group-games.component';
import { SettingGroupRouterComponent } from './components/routes/setting-group-router/setting-group-router.component';
import { SettingGroupGeneralComponent } from './components/site/setting-group-general/setting-group-general.component';
import { SettingGroupMembersComponent } from './components/site/setting-group-members/setting-group-members.component';
import { GameCreateComponent } from './components/site/game-create/game-create.component';
import { GroupCreateComponent } from './components/site/group-create/group-create.component';
import { RegisterTokenComponent } from './components/site/register-token/register-token.component';
import { SettingGroupLocationsComponent } from './components/site/setting-group-locations/setting-group-locations.component';

export const routes: Routes = [
    {
        path: "barrier",
        component: AuthRouterComponent,
        children: [
            { path: "register", component: RegisterComponent, pathMatch: "full" },
            { path: "register/:token", component: RegisterTokenComponent, pathMatch: "full" },
            { path: "login", component: LoginComponent, pathMatch: "full" },
            { path: "login/totp", component: TotpComponent, pathMatch: "full" },
            { path: "logout", component: LogoutComponent, pathMatch: "full" },
        ]
    },
    { path: "game/active/:uuid", component: GameInputComponent, pathMatch: "full" },
    {
        path: "",
        component: MainRouterComponent,
        children: [
            { path: "", component: HomeComponent, pathMatch: "full" },
            { path: "profile", component: ProfileComponent, pathMatch: "full" },
            { path: "profile/:username", component: ProfileComponent, pathMatch: "full" },
            { 
                path: "settings",
                component: SettingAccountRouterComponent,
                children: [
                    { path: "", redirectTo: "profile", pathMatch: "full" },
                    { path: "profile", component: SettingAccountProfileComponent, pathMatch: "full" },
                    { path: "account", component: SettingAccountAccountComponent, pathMatch: "full" },
                ]
            },
            { path: "group", component: GroupListComponent, pathMatch: "full" },
            { path: "group/new", component: GroupCreateComponent, pathMatch: "full" },
            {
                path: "group/:uuid/settings",
                component: SettingGroupRouterComponent,
                children: [
                    { path: "", redirectTo: "general", pathMatch: "full" },
                    { path: "general", component: SettingGroupGeneralComponent, pathMatch: "full" },
                    { path: "members", component: SettingGroupMembersComponent, pathMatch: "full" },
                    { path: "locations", component: SettingGroupLocationsComponent, pathMatch: "full" }
                ]
            },
            { 
                path: "group/:uuid",
                component: GroupComponent,
                children: [
                    { path: "", component: GroupOverviewComponent, pathMatch: "full" },
                    { path: "games", component: GroupGamesComponent, pathMatch: "full" },
                ]
            },
            { path: "game/new", component: GameCreateComponent, pathMatch: "full" },
        ]
    },
    { path: "**", redirectTo: "", pathMatch: "full" }
];
