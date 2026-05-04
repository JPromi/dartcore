import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { GroupLightResponse } from '../../../dtos/groupLightResponse';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { GroupService } from '../../../services/group.service';
import { PageResponse } from '../../../dtos/pageResponse';
import { GroupInvitationResponse } from '../../../dtos/groupInvitationResponse';
import { InvitationStatusAccountEnum } from '../../../enums/invitationStatusAccountEnum';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-group-list',
  imports: [
    CommonModule,
    FormsModule,
    TranslateModule,
    RouterModule,
    FontAwesomeModule
  ],
  templateUrl: './group-list.component.html',
  styleUrl: './group-list.component.scss',
  animations: [
    trigger(
      'fade', [
        transition(':enter', [
          style({ opacity: 0, transform: 'translateY(98%)' }),
          animate('100ms', style({ opacity: 1, transform: 'translateY(100%)' }))
        ]),
        transition(':leave', [
          style({ opacity: 1, transform: 'translateY(100%)' }),
          animate('150ms', style({ opacity: 0, transform: 'translateY(98%)' }))
        ])
      ]
    )
  ]
})
export class GroupListComponent implements OnInit {

  constructor(
    private groupService: GroupService,
    private elementRef: ElementRef
  ) { }

  fa = fa;

  public groups: GroupLightResponse[] = [];
  public searchQuery: string = '';
  public groupInvitationCount: number = 0;
  public groupInvitation: GroupInvitationResponse[] = [];
  public isInvitePopupOpen: boolean = false;

  private lastKeyPress: Date = new Date();
  private lastUpdateIntervall: number = .5; // seconds
  private lastUpdateTimeout: any = null;

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isInvitePopupOpen = false;
    }
  }

  ngOnInit(): void {
    this._searchGroup();
    this.getGroupInvitationCount();
  }

  public getMemberCount(total: number): string {
    if (total < 1000) {
      return total.toString();
    } else if (total < 1000000) {
      return Math.floor(total / 1000) + 'k';
    } else if (total < 1000000000) {
      return Math.floor(total / 1000000) + 'M';
    } else {
      return '+ 100M';
    }
  }

  public getInvitationCount(total: number): string {
    if (total >= 10) {
      return '+ 10';
    } else {
      return total.toString();
    }
  }

  public clearSearch() {
    if(this.searchQuery != "") {
    this.searchQuery = '';
    this._searchGroup();
    }
  }

  public searchGroup() {
    if (!this.lastUpdateTimeout) {
      this.lastUpdateTimeout = setTimeout(() => {
        if (this.lastKeyPress.getTime() + this.lastUpdateIntervall * 1000 < new Date().getTime()) {
          this._searchGroup();
        }
        this.lastUpdateTimeout = null;
      }, this.lastUpdateIntervall * 1000);
    }
  }

  public sendInvitationResponse(invitation: GroupInvitationResponse, accept: boolean) {
    this.groupService.sendInvitationResponse(invitation.uuid!, accept ? InvitationStatusAccountEnum.ACCEPTED : InvitationStatusAccountEnum.REJECTED).subscribe(
      () => {
        this.getGroupInvitationCount();
        this._searchGroup();
      }
    )
  }

  public findActiveInvitationForGroup(groupUuid: string): GroupInvitationResponse | null {
    for (let invitation of this.groupInvitation) {
      if (invitation.group?.uuid == groupUuid && invitation.status == InvitationStatusAccountEnum.PENDING) {
        return invitation;
      }
    }
    return null;
  }

  private _searchGroup() {
    this.groupService.searchGroup(this.searchQuery, this.searchQuery == "" ? true : null).subscribe(
      (response: PageResponse<GroupLightResponse>) => {
        this.groups = response.content;
      }
    );
  }

  private getGroupInvitationCount() {
    this.groupService.getInvitationList().subscribe(
      (response: GroupInvitationResponse[]) => {
        this.groupInvitationCount = response.length;
        this.groupInvitation = response;
      }
    );
  }
}
