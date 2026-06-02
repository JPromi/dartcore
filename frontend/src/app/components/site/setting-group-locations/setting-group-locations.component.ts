import { Component, HostListener } from '@angular/core';
import { PageResponse } from '../../../dtos/pageResponse';
import { GroupResponse } from '../../../dtos/groupResponse';
import { GroupService } from '../../../services/group.service';
import { ActivatedRoute } from '@angular/router';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { LocationResponse } from '../../../dtos/locationResponse';
import { animate, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { LocationResourceResponse } from '../../../dtos/locationResourceResponse';
import { PopupComponent } from "../../assets/popup/popup.component";

@Component({
  selector: 'app-setting-group-locations',
  imports: [
    FontAwesomeModule,
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    PopupComponent
],
  templateUrl: './setting-group-locations.component.html',
  styleUrl: './setting-group-locations.component.scss',
  animations: [
    trigger(
      'fade', [
        transition(':enter', [
          style({ opacity: 0 }),
          animate('100ms', style({ opacity: 1 }))
        ]),
        transition(':leave', [
          style({ opacity: 1 }),
          animate('150ms', style({ opacity: 0 }))
        ])
      ]
    )
  ]
})
export class SettingGroupLocationsComponent {
constructor(
    private groupService: GroupService,
    private activeRoute: ActivatedRoute
  ) { }

  public groupData: GroupResponse | null = null;
  public locations: LocationResponse[] = [];

  public showDeletePopup: boolean = false;

  public selectedLocation: LocationResponse | null = null;
  public selectedLocationUpdating: boolean = false;
  public selectedLocationScreen: LocationResourceResponse | null = null;
  public selectedLocationClient: LocationResourceResponse | null = null;

  selectedLocationForm: FormGroup = new FormGroup(
    {
      name: new FormControl<string>("",  [Validators.required]),
      description: new FormControl<string>(""),
      address: new FormControl<string>(""),
      isPublic: new FormControl<boolean>(false),
    }
  );

  selectedLocationScreenForm: FormGroup = new FormGroup(
    {
      name: new FormControl<string>("",  [Validators.required]),
    }
  );

  selectedLocationClientForm: FormGroup = new FormGroup(
    {
      name: new FormControl<string>("",  [Validators.required]),
    }
  );

  fa = fa;

  ngOnInit(): void {
    this.activeRoute.parent?.params.subscribe(
      (params) => {
        const groupUuid = params['uuid'];
        if (groupUuid) {
          this._loadGroupData(groupUuid);
        }
      }
    );
  }

  public copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      // Optionally, you can show a success message or perform other actions after copying
    }).catch(err => {
      console.error('Could not copy text: ', err);
    });
  }

  public createLocation() {
    if (this.groupData) {
      const newLocation = new LocationResponse();

      this.selectedLocation = newLocation;
      this.selectedLocationForm.setValue({
        name: "",
        description: "",
        address: "",
        isPublic: true
      });
    }
  }

  public openLocationPopup(location: LocationResponse) {
    this.selectedLocation = location;
    this.selectedLocationForm.setValue({
      name: location.name ?? "",
      description: location.description ?? "",
      address: location.address ?? "",
      isPublic: location.isPublic
    });
  }

  public closeLocationPopup() {
    this.selectedLocation = null;
    this.selectedLocationForm.reset();
  }

  public saveLocation() {
    if (this.selectedLocation) {
      this.selectedLocationUpdating = true;
      const updatedLocation = new LocationResponse(
        this.selectedLocation.uuid,
        this.selectedLocationForm.get('name')?.value,
        this.selectedLocationForm.get('description')?.value,
        this.selectedLocationForm.get('isPublic')?.value,
        this.selectedLocationForm.get('address')?.value,
        this.selectedLocation.screens,
        this.selectedLocation.clients
      );
      if (!this.selectedLocation.uuid) {
        this.groupService.createLocation(this.groupData!.uuid, updatedLocation).subscribe({
          next: (response) => {
            this.closeLocationPopup();
            this._loadLocations(this.groupData!.uuid);
            this.selectedLocationUpdating = false;
          },
          error: (error) => {
            console.error(error);
            this.selectedLocationUpdating = false;
          }
        });

      } else {
        this.groupService.saveLocation(this.groupData!.uuid, this.selectedLocation.uuid, updatedLocation).subscribe({
          next: (response) => {
            this.closeLocationPopup();
            this._loadLocations(this.groupData!.uuid);
            this.selectedLocationUpdating = false;
          },
          error: (error) => {
            console.error(error);
            this.selectedLocationUpdating = false;
          }
        });

      }
    }
  }

  deleteLocation(confirmed: boolean) {
    if (confirmed && this.selectedLocation) {
      this.groupService.deleteGroupLocation(this.groupData!.uuid, this.selectedLocation.uuid).subscribe({
        next: () => {
          this._loadLocations(this.groupData!.uuid);
          this.closeLocationPopup();
        },
        error: (error) => {
          console.error("Error deleting location", error);
        }
      });
    }
  }

  public getFirstErrorKey(controlName: string | null | undefined, form: FormGroup): string | null {
    if(controlName) {
      const errors = form.get(controlName)?.errors;
      return errors ? Object.keys(errors)[0] : null;
    } else {
      return null;
    }
  }

  // screen
  public createTmpScreen(location: LocationResponse) {
    if (this.selectedLocation) {
      this.selectedLocationUpdating = true;
      this.groupService.createTmpGroupLocationScreen(this.groupData!.uuid, location.uuid).subscribe({
        next: (response) => {
          this.openLocationScreenPopup(response);
          this.selectedLocationUpdating = false;
        },
        error: (error) => {
          console.error(error);
          this.selectedLocationUpdating = false;
        }
      });
    }
  }

  public openLocationScreenPopup(screen: LocationResourceResponse) {
    this.selectedLocationScreen = screen;
    this.selectedLocationScreenForm.setValue({
      name: screen.name ?? ""
    });
  }

  public closeLocationScreenPopup() {
    this.selectedLocationScreen = null;
    this.selectedLocationScreenForm.reset();
  }

  public saveLocationScreen() {
    if (this.selectedLocation && this.selectedLocationScreen) {
      const existingScreenIndex = this.selectedLocation.screens.findIndex(s => s.uuid === this.selectedLocationScreen!.uuid);
      if (existingScreenIndex !== -1) {
        this.selectedLocation.screens[existingScreenIndex].name = this.selectedLocationScreenForm.get('name')?.value;
      } else {
        this.selectedLocation.screens.push(new LocationResourceResponse(this.selectedLocationScreen.uuid, this.selectedLocationScreenForm.get('name')?.value, this.selectedLocationScreen.token));
      }
      this.closeLocationScreenPopup();
    }
  }

  public deleteLocationScreen() {
    if (this.selectedLocation && this.selectedLocationScreen) {
      const existingScreenIndex = this.selectedLocation.screens.findIndex(s => s.uuid === this.selectedLocationScreen!.uuid);
      if (existingScreenIndex !== -1) {
        this.selectedLocation.screens.splice(existingScreenIndex, 1);
      }
      this.closeLocationScreenPopup();
    }
  }

  public getScreenTokenUlr(screen: LocationResourceResponse): string {
    return `${window.location.origin}/screen/${screen.token}`;
  }

  // client
  public openLocationClientPopup(client: LocationResourceResponse) {
    this.selectedLocationClient = client;
    this.selectedLocationClientForm.setValue({
      name: client.name ?? ""
    });
  }

  public closeLocationClientPopup() {
    this.selectedLocationClient = null;
    this.selectedLocationClientForm.reset();
  }

  public saveLocationClient() {
    if (this.selectedLocation && this.selectedLocationClient) {
      const existingClientIndex = this.selectedLocation.clients.findIndex(c => c.uuid === this.selectedLocationClient!.uuid);
      if (existingClientIndex !== -1) {
        this.selectedLocation.clients[existingClientIndex].name = this.selectedLocationClientForm.get('name')?.value;
      } else {
        this.selectedLocation.clients.push(new LocationResourceResponse(this.selectedLocationClient.uuid, this.selectedLocationClientForm.get('name')?.value, this.selectedLocationClient.token));
      }
      this.closeLocationClientPopup();
    }
  }

  public createTmpClient(location: LocationResponse) {
    if (this.selectedLocation) {
      this.selectedLocationUpdating = true;
      this.groupService.createTmpGroupLocationClient(this.groupData!.uuid, location.uuid).subscribe({
        next: (response) => {
          this.openLocationClientPopup(response);
          this.selectedLocationUpdating = false;
        },
        error: (error) => {
          console.error(error);
          this.selectedLocationUpdating = false;
        }
      });
    }
  }

  public deleteLocationClient() {
    if (this.selectedLocation && this.selectedLocationClient) {
      const existingClientIndex = this.selectedLocation.clients.findIndex(c => c.uuid === this.selectedLocationClient!.uuid);
      if (existingClientIndex !== -1) {
        this.selectedLocation.clients.splice(existingClientIndex, 1);
      }
      this.closeLocationClientPopup();
    }
  }

  public getClientTokenUlr(client: LocationResourceResponse): string {
    return `${window.location.origin}/client/${client.token}`;
  }

  private _loadGroupData(groupUuid: string): void {
    this.groupService.getGroup(groupUuid).subscribe({
      next: (response) => {
        this.groupData = response;
        this._loadLocations(groupUuid);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  private _loadLocations(groupUuid: string): void {
    this.groupService.getGroupLocations(groupUuid).subscribe({
      next: (response) => {
        this.locations = response;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  @HostListener('keydown.escape')
  onKeydownHandler() {
    // this.closeMemberPopup();
    // this.showInviteMembers = false;
  }
}
