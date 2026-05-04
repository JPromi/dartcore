import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import * as fa from '@fortawesome/free-solid-svg-icons';
import { CommonModule } from '@angular/common';
import { LoValidationService } from '../../../services/local/lo-validation.service';
import { SettingAccount } from '../../../entities/settingAccount';
import { SettingsService } from '../../../services/settings.service';

@Component({
  selector: 'app-setting-account-account',
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    FontAwesomeModule
  ],
  templateUrl: './setting-account-account.component.html',
  styleUrl: './setting-account-account.component.scss'
})
export class SettingAccountAccountComponent implements OnInit {

  constructor(
    private loValidationService: LoValidationService,
    private settingsService: SettingsService
  ) {}

  fa = fa;

  account: SettingAccount = new SettingAccount();
  isLoaded: boolean = false;
  isUpdating: boolean = false;

  form: FormGroup = new FormGroup(
    {
      firstName: new FormControl<string>("", [Validators.required]),
      lastName: new FormControl<string>(""),
      email: new FormControl<string>("", [Validators.required, Validators.email]),
      passwordOld: new FormControl<string>(""),
      password: new FormControl<string>(""),
      passwordConfirm: new FormControl<string>(""),
    },
    this.loValidationService.passwordMatch('password', 'passwordConfirm')
  );

  ngOnInit(): void {
    this.getAccount();
  }

  submit() {
    if (this.form.valid) {
      this.isUpdating = true;
      this.account.firstName = this.form.value.firstName;
      this.account.lastName = this.form.value.lastName;
      this.account.email = this.form.value.email;
      this.account.passwordOld = this.form.value.passwordOld;
      this.account.passwordNew = this.form.value.password;
      this.account.passwordNewConfirm = this.form.value.passwordConfirm;
      
      this.settingsService.updateAccountSettings(this.account).subscribe({
        next: () => {
          this.isUpdating = false;
          console.log("Account updated successfully");
        },
        error: (error) => {
          console.error(error);
        }
      });
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

  private getAccount() {
    this.isLoaded = false;
    this.settingsService.getAccountSettings().subscribe({
      next: (account) => {
        this.account = account;
        this.form.patchValue({
          firstName: account.firstName,
          lastName: account.lastName,
          email: account.email,
        });
        this.isLoaded = true;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }
  
}
