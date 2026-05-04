import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { GroupResponse } from '../../../dtos/groupResponse';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { GroupService } from '../../../services/group.service';
import { PopupComponent } from '../../assets/popup/popup.component';

@Component({
  selector: 'app-group',
  imports: [
    CommonModule,
    TranslateModule,
    RouterModule,
    FontAwesomeModule,
    PopupComponent
  ],
  templateUrl: './group.component.html',
  styleUrl: './group.component.scss',
  animations: [
    // width animation
    trigger('profileExpand', [
      state('none', style({ width: '0', display: 'none' })),
      state('small', style({ width: '2.5rem' })),
      state('small-outside', style({ width: '1.5rem' })),
      state('large-outside', style({ width: '*' })),
      state('large', style({ width: '*' })),
      transition('small <=> large', [
        animate('500ms ease-in-out')
      ]),
      transition('none <=> large', [
        animate('500ms ease-in-out')
      ]),
      transition('small-outside <=> large-outside', [
        animate('500ms ease-in-out')
      ]),
      transition('none <=> large-outside', [
        animate('500ms ease-in-out')
      ]),
    ]),
  ]
})
export class GroupComponent implements OnInit {

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private groupService: GroupService
  ) { }

  public membersPreviewMax: number = 5;
  public isLargeMembersPreview: Boolean = false;
  public activeSubMenu: string = '';
  public fa = fa;
  public group?: GroupResponse;

  public showLeaveGroupPopup: boolean = false;

  ngOnInit(): void {
    this.activatedRoute.url.subscribe(
      (url) => {
        this.activeSubMenu = url[2]?.path ?? '';
      }
    )

    this.activatedRoute.params.subscribe(
      (params) => {
        const uuid = params['uuid'];
        if (uuid) {
          this.getGroup(uuid);
        } else {
          this.router.navigate(['/']);
        }
      }
    );
  }

  leaveGroup(confirmed: boolean) {
    if (!this.group?.isMember || this.group?.isOwner) return;
    if (!confirmed) return;


    this.groupService.leaveGroup(this.group.uuid).subscribe(
      () => {
        this.router.navigate(['/group']);
      }
    );
  }

  private getGroup(uuid: string): void {
    this.groupService.getGroup(uuid).subscribe(
      (response: GroupResponse) => {
        this.group = response;
      }
    );
  }

  navigateToNewRoute(event: any, newUrl: any) {
    event.preventDefault(); // Verhindern, dass die Seite neu geladen wird
    this.router.navigateByUrl(newUrl); // Navigiere zur neuen URL
  }

}
