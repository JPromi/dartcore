import { Component, Host, HostListener, OnInit } from '@angular/core';
import { GroupService } from '../../../services/group.service';
import { GroupAdminMember } from '../../../dtos/groupAdminMember';
import { ActivatedRoute } from '@angular/router';
import { GroupResponse } from '../../../dtos/groupResponse';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { InputCheckboxSliderComponent } from '../../assets/input-checkbox-slider/input-checkbox-slider.component';
import { GroupMemberAdminRequest } from '../../../dtos/groupMemberAdminRequest';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../../../services/profile.service';
import { ProfileLightResponse } from '../../../dtos/profileLightResponse';
import { PageResponse } from '../../../dtos/pageResponse';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-setting-group-members',
  imports: [
    CommonModule,
    FontAwesomeModule,
    InputCheckboxSliderComponent,
    TranslateModule,
    FormsModule
  ],
  templateUrl: './setting-group-members.component.html',
  styleUrl: './setting-group-members.component.scss',
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
export class SettingGroupMembersComponent implements OnInit {
  constructor(
    private groupService: GroupService,
    private profileService: ProfileService,
    private activeRoute: ActivatedRoute
  ) { }

  public groupData: GroupResponse | null = null;
  public groupMembers: GroupAdminMember[] = [];
  public selectedMember: GroupAdminMember | null = null;
  public removeMember: boolean = false;

  public showInviteMembers: boolean = false;
  public inviteSearchTerm: string = '';
  private lastInvitationSearchKeyPress: Date = new Date();
  public invitationSearchResults: PageResponse<ProfileLightResponse> | null = null;
  private lastInvitationSearchUpdateIntervall: number = .5; // seconds
  private lastInvitationSearchUpdateTimeout: any = null;

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

  public saveMemberChanges() {
    if (!this.selectedMember || !this.groupData) return;

    const request: GroupMemberAdminRequest = {
      isAdmin: this.selectedMember.isAdmin
    }

    this.groupService.saveMemberChanges(this.groupData.uuid, this.selectedMember.uuid, request).subscribe({
      next: () => {
        this._loadGroupData(this.groupData?.uuid!);
        this.closeMemberPopup();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  public removeMemberFromGroup() {
    if (!this.selectedMember || !this.groupData) return;

    this.groupService.removeMemberFromGroup(this.groupData.uuid, this.selectedMember.uuid).subscribe({
      next: () => {
        this._loadGroupData(this.groupData?.uuid!);
        this.closeMemberPopup();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  public removeInvitation() {
    if (!this.selectedMember || !this.groupData) return;

    this.groupService.removeInvitationFromGroup(this.groupData.uuid, this.selectedMember.uuid).subscribe({
      next: () => {
        this._loadGroupData(this.groupData?.uuid!);
        this.closeMemberPopup();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  public closeMemberPopup() {
    this.selectedMember = null;
    this.removeMember = false;
  }

  public invitePlayer(profile: ProfileLightResponse) {
    if (!this.groupData) return;

    this.groupService.inviteMember(this.groupData.uuid, profile.uuid).subscribe({
      next: () => {
        this._loadGroupData(this.groupData?.uuid!);
        this.inviteSearchTerm = '';
        this.invitationSearchResults = null;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  public searchProfileInvitation() {
    if (!this.lastInvitationSearchUpdateTimeout) {
      this.lastInvitationSearchUpdateTimeout = setTimeout(() => {
        if (this.lastInvitationSearchKeyPress.getTime() + this.lastInvitationSearchUpdateIntervall * 1000 < new Date().getTime()) {
          this._searchProfileInvitation();
        }
        this.lastInvitationSearchUpdateTimeout = null;
      }, this.lastInvitationSearchUpdateIntervall * 1000);
    }
  }

  private _searchProfileInvitation() {
    if (this.inviteSearchTerm.trim() === '') {
      this.invitationSearchResults = null;
      return;
    } else {
      this.profileService.searchProfile(this.inviteSearchTerm, 0, 10, false, null, this.groupData?.uuid).subscribe({
        next: (response) => {
          this.invitationSearchResults = response;
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }

  public clearSearchProfileInvitation() {
    this.inviteSearchTerm = '';
  }

  private _loadGroupData(groupUuid: string): void {
    this.groupService.getGroup(groupUuid).subscribe({
      next: (response) => {
        this.groupData = response;
        if (response.isAdmin || response.isOwner) {
          this._getAdminGroupMembers(groupUuid);
        } else {
          this.groupMembers = [];
        }
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  private _getAdminGroupMembers(groupUuid: string): void {
    this.groupService.getAdminGroupMembers(groupUuid).subscribe({
      next: (response) => {
        this.groupMembers = response;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  @HostListener('keydown.escape')
  onKeydownHandler() {
    this.closeMemberPopup();
    this.showInviteMembers = false;
  }
}
