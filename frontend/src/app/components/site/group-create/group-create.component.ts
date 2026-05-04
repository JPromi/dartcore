import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { TranslateModule } from '@ngx-translate/core';
import { GroupEditRequest } from '../../../dtos/groupEditRequest';
import { GroupService } from '../../../services/group.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-group-create',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    TranslateModule
  ],
  templateUrl: './group-create.component.html',
  styleUrl: './group-create.component.scss'
})
export class GroupCreateComponent {

  constructor(
    private groupService: GroupService,
    private router: Router
  ) { }

  isLoaded = true;
  isUpdating = false;
  fa = fa;

  groupData: GroupEditRequest = new GroupEditRequest();

  form: FormGroup = new FormGroup(
    {
      name: new FormControl<string>("", [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-zA-Z0-9_-]/)]),
      description: new FormControl<string>(""),
      isPublic: new FormControl<boolean>(true),
    }
  );

  submit() {
    if (this.form.valid) {
      this.isUpdating = true;
      this.groupData.name = this.form.value.name;
      this.groupData.description = this.form.value.description;
      this.groupData.isPublic = this.form.value.isPublic;

      this.groupService.createGroup(this.groupData).subscribe({
        next: (response) => {
          this.isUpdating = false;
          console.log("Group created successfully");
          this.router.navigate(['/group', response.uuid]);
        },
        error: (error) => {
          console.error(error);
        }
      });
      
      // this.settingsService.updateProfileSettings(this.profile).subscribe({
      //   next: () => {
      //     this.isUpdating = false;
      //     console.log("Profile updated successfully");
      //   },
      //   error: (error) => {
      //     console.error(error);
      //   }
      // });
    }
  }

  getFirstErrorKey(controlName: string | null | undefined): string | null {
    if(controlName) {
      const errors = this.form.get(controlName)?.errors;
      return errors ? Object.keys(errors)[0] : null;
    } else {
      return null;
    }
  }
}
