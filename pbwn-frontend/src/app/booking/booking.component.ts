import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Component, inject, model, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialog, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { DateFilterFn, MatDatepickerInputEvent, MatDatepickerModule } from '@angular/material/datepicker';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [
    FormsModule,
    MatInputModule,
    MatSelectModule,
    MatFormFieldModule,
    MatButtonModule,
    MatDatepickerModule,
    MatCardModule,
    MatChipsModule,
    CommonModule
  ],
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.scss'
})
export class BookingComponent implements OnInit {
  BOOKING_API_URL = '/api/booking';

  locations = {};
  location: string = '';
  booking: IBooking | undefined;
  showBooking: boolean = false;
  bookingDates: Date[] = [];
  pickedDate: string | undefined;
  showCalendar: boolean = false;
  slotEnrolls: ISlotEnroll[] = [];
  dialog = inject(MatDialog);
  names = signal([]);


  constructor(
    private httpClient: HttpClient
  ) {

  }

  ngOnInit(): void {
    this.httpClient.get(`${this.BOOKING_API_URL}/locations`)
      .subscribe(
        res => {
          this.locations = res;
        }
      );
  }

  getBooking(): void {
    this.showCalendar = false;
    this.showBooking = false;
    this.bookingDates = [];
    this.slotEnrolls = [];
    this.httpClient.get<IBooking>(`${this.BOOKING_API_URL}/${this.location}`)
      .subscribe(
        res => {
          this.booking = res;
          this.bookingDates = Object.keys(this.booking.slotEnrolls).map((k: string) => { return new Date(k) });
          this.showCalendar = true;
        }
      );
  }

  filterDate: DateFilterFn<Date | null> = (date: Date | null): boolean => {
    return !!this.bookingDates.find(d => this.formatDate(d) === this.formatDate(date));
  }

  getDateBooking(event: MatDatepickerInputEvent<Date>): void {
    this.showBooking = false;
    this.slotEnrolls = [];
    const date = this.formatDate(event.value);
    this.slotEnrolls = this.booking?.slotEnrolls[date];
    this.showBooking = true;
  }

  formatDate(pickedDate: Date | null): string {
    if (!pickedDate) {
      return '';
    }
    const year = pickedDate.getFullYear();
    const month = ((pickedDate.getMonth() + 1) < 10 ? '0' : '') + (pickedDate.getMonth() + 1);
    const date = (pickedDate.getDate() < 10 ? '0' : '') + pickedDate.getDate();
    return `${year}-${month}-${date}`;
  }

  getNamesJoin(names: string[]): string {
    return names.join(', ');
  }

  openDialog(se: ISlotEnroll): void {
    const dialogRef = this.dialog.open(BookingNameDialog, {
      data: { se, names: this.names() },
    });

    dialogRef.afterClosed().subscribe((result: string[]) => {
      if (result !== undefined) {
        se.names = result.filter(n => n != null && n.length > 0);
        this.httpClient.post<ISlotEnroll>(`${this.BOOKING_API_URL}/${this.location}`, se)
          .subscribe(
            res => {
              se.names = res.names;
            }
          );
        this.names = signal([]);
      }
    });
  }
}

@Component({
  selector: 'booking-name-dialog',
  templateUrl: 'booking-name-dialog.html',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
  ],
})
export class BookingNameDialog {
  dialogRef = inject(MatDialogRef<BookingNameDialog>);
  data = inject<any>(MAT_DIALOG_DATA);
  names = model(this.data.names);

  onNoClick(): void {
    this.dialogRef.close();
  }
}

export interface IBooking {
  location: string;
  slotEnrolls: any;
  lastSlotUpdate: number[];
}

export interface ISlotEnroll {
  date: string;
  day: string;
  slot: string;
  names: string[];
}
