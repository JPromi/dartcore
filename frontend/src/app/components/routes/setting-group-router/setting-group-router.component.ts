import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule, RouterOutlet } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { CommonModule } from '@angular/common';
import { GroupService } from '../../../services/group.service';
import { GroupResponse } from '../../../dtos/groupResponse';

@Component({
  selector: 'app-setting-group-router',
  imports: [
    RouterOutlet,
    RouterModule,
    TranslateModule,
    FontAwesomeModule,
    CommonModule
  ],
  templateUrl: './setting-group-router.component.html',
  styleUrl: './setting-group-router.component.scss'
})
export class SettingGroupRouterComponent implements OnInit {

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private groupService: GroupService
  ) { }

  fa = fa;

  uuid: string = "";

  group: GroupResponse | null = null;

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      (params) => {
        const uuid = params['uuid'];
        if (uuid) {
          this.uuid = uuid;
          this.loadGroupData();
        } else {
          this.router.navigate(['/']);
        }
      }
    );
  }

  private loadGroupData() {
    this.groupService.getGroup(this.uuid).subscribe((response) => {
      this.group = response;

      if (!this.group.isAdmin && !this.group.isOwner) {
        this.router.navigate(['/']);
      }
    });
  }
}
