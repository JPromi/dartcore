import { Component, HostListener } from '@angular/core';
import { PageResponse } from '../../../dtos/pageResponse';
import { GroupResponse } from '../../../dtos/groupResponse';
import { GroupService } from '../../../services/group.service';
import { ActivatedRoute } from '@angular/router';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { LocationResponse } from '../../../dtos/LocationResponse';
import { animate, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-setting-group-locations',
  imports: [
    FontAwesomeModule,
    CommonModule,
    TranslateModule,
    ReactiveFormsModule
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

  public selectedLocation: LocationResponse | null = null;

  selectedLocationForm: FormGroup = new FormGroup(
    {
      name: new FormControl<string>("",  [Validators.required]),
      description: new FormControl<string>(""),
      address: new FormControl<string>(""),
      isPublic: new FormControl<boolean>(false),
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

  public getFirstErrorKey(controlName: string | null | undefined, form: FormGroup): string | null {
    if(controlName) {
      const errors = form.get(controlName)?.errors;
      return errors ? Object.keys(errors)[0] : null;
    } else {
      return null;
    }
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
